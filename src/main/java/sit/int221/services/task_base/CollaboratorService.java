package sit.int221.services.task_base;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.dtos.request.NewCollaboratorDTO;
import sit.int221.dtos.response.CollaboratorDTORes;
import sit.int221.dtos.response.EditAccessRightDTO;
import sit.int221.dtos.response.NewCollabDTORes;
import sit.int221.entities.enums.InvitationStatus;
import sit.int221.entities.task_base.Board;
import sit.int221.entities.task_base.Collaborator;
import sit.int221.entities.task_base.LocalUser;
import sit.int221.entities.itbkk_shared.User;
import sit.int221.repositories.task_base.CollaboratorRepository;
import sit.int221.repositories.task_base.LocalUserRepository;
import sit.int221.repositories.itbkk_shared.UserRepository;
import sit.int221.services.EmailService;
import sit.int221.services.itbkk_shared.AuthorizationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollaboratorService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private LocalUserRepository localUserRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    AuthorizationService authorizationService;


    // fix to use listMapper or
    public List<CollaboratorDTORes> getAllCollaborators(Claims claims, String boardId) {
        Board board = authorizationService.getBoardId(boardId);
        validateAccess(claims, board);
        List<Collaborator> collaborators = collaboratorRepository.findAllByBoardId(boardId);
        return collaborators.stream().map(collaborator -> getCollabResDTO(collaborator, collaborator.getLocalUser())).collect(Collectors.toList());
    }

    public List<CollaboratorDTORes> getAllCollaborators(String boardId) {
        List<Collaborator> collaborators = collaboratorRepository.findAllByBoardId(boardId);
        return collaborators.stream().map(collaborator -> getCollabResDTO(collaborator, collaborator.getLocalUser())).collect(Collectors.toList());
    }

    public CollaboratorDTORes getCollabById(Claims claims, String boardId, String oid) {
        Board board = authorizationService.getBoardId(boardId);
        validateAccess(claims, board);
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        if (!isCollaborator) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found");
        }
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(boardId, oid);
        return getCollabResDTO(collaborator, collaborator.getLocalUser());
    }

    public CollaboratorDTORes getCollabById(String boardId, String oid) {
        Board board = authorizationService.getBoardId(boardId);
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(boardId, oid);
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        if (!isCollaborator) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found");
        }
        return getCollabResDTO(collaborator, collaborator.getLocalUser());
    }

    @Transactional(transactionManager = "taskBaseTransactionManager")
    public NewCollabDTORes createNewCollaborator(Claims claims, String boardId, NewCollaboratorDTO newCollab) {
        String oid = (String) claims.get("oid");
        String email = (String) claims.get("email");
        String name = (String) claims.get("name");
        // Retrieve the board by boardId, throws exception if board does not exist
        Board board = authorizationService.getBoardId(boardId);

        // check  email exists in share_itbkk
        User existsEmailShared = userRepository.findByEmail(newCollab.getEmail());

        // check  email exists in localUser database
        Boolean existsEmailCollab = collaboratorRepository.existsByBoardAndLocalUserEmail(board, newCollab.getEmail());

        // check if oid is the owner of the board
        if (oid.equals(board.getOwnerId())) {

            // Validate accessRight fields
            if (newCollab.getAccessRight() == null || newCollab.getAccessRight().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right cannot be null or empty");
            }

            // Validate email
            if (newCollab.getEmail() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null");
            }

            if (!newCollab.getAccessRight().equalsIgnoreCase("READ") && !newCollab.getAccessRight().equalsIgnoreCase("WRITE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right must be either 'READ' or 'WRITE'");
            }

            // e-mail.NOT.exists.in.itbkk_shared
            if (existsEmailShared == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email does not exist in itbkk_shared");
            }

            // Check if the user is trying to add themselves as a collaborator
            if (email.equalsIgnoreCase(newCollab.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot add yourself as a collaborator");
            }

            // Check if the user is already a collaborator on the board
            if (existsEmailCollab) {
                Optional<Collaborator> existingCollaborator = collaboratorRepository.findByBoardIdAndLocalUserEmail(board.getId(), newCollab.getEmail());
                existingCollaborator.ifPresent(collaborator -> {
                    if (collaborator.getInvitationStatus() == InvitationStatus.PENDING || collaborator.getInvitationStatus() == InvitationStatus.ACCEPTED) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a collaborator or pending collaborator on this board");
                    }
                });
            }

            // Fetch the LocalUser by email
            LocalUser localUser = localUserRepository.findByEmail(newCollab.getEmail());
            if (localUser == null) {
                LocalUser newLocalUser = new LocalUser();
                newLocalUser.setOid(existsEmailShared.getOid());
                newLocalUser.setName(existsEmailShared.getName());
                newLocalUser.setUsername(existsEmailShared.getUsername());
                newLocalUser.setEmail(existsEmailShared.getEmail());
                localUser = localUserRepository.save(newLocalUser);
            }

            Collaborator newCollaborator = new Collaborator();
            newCollaborator.setBoard(board);
            newCollaborator.setLocalUser(localUser);
            newCollaborator.setInvitationStatus(InvitationStatus.PENDING);
            //  must accept before get accessRight
            newCollaborator.setAccessRight(newCollab.getAccessRight());

            collaboratorRepository.save(newCollaborator);

            emailService.sendEmail("itbkkttone@gmail.com",
//                    newCollab.getEmail(),
                    name,
                    newCollab.getAccessRight(),
                    board.getName(),
                    board.getId());

            // create responseDTO
            return collaboratorResponse(boardId, localUser, newCollaborator.getAccessRight(), newCollaborator.getInvitationStatus());

        } else {
            // token.is.valid AND board($id).exists AND token.oid.is.NOT.board.owner return 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Board Owner");
        }
    }

    public NewCollabDTORes acceptInvitation(Claims claims, String boardId) {
        String oid = (String) claims.get("oid");
        // Retrieve the board by boardId, throws exception if board does not exist
        Board board = authorizationService.getBoardId(boardId);

        // find collaborator on board with oid
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(board.getId(), oid);

        // Check if the collaborator is in PENDING status
        if (collaborator == null || collaborator.getInvitationStatus() != InvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending invitation for this collaborator");
        }

        if (collaborator.getInvitationStatus() == InvitationStatus.PENDING) {
            // Set invitation status to ACCEPTED
            collaborator.setInvitationStatus(InvitationStatus.ACCEPTED);
            collaboratorRepository.save(collaborator);
        }

        LocalUser localUser = localUserRepository.findByEmail(collaborator.getLocalUser().getEmail());

        return collaboratorResponse(boardId, localUser,
                collaborator.getAccessRight(), collaborator.getInvitationStatus());
    }

    public NewCollabDTORes declineInvitation(Claims claims, String boardId) {
        String oid = (String) claims.get("oid");
        // Retrieve the board by boardId, throws exception if board does not exist
        Board board = authorizationService.getBoardId(boardId);

        // find collaborator on board with oid
        Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(board.getId(), oid);

        // Check if the collaborator is in PENDING status
        if (collaborator == null || collaborator.getInvitationStatus() != InvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending invitation for this collaborator");
        }

        collaboratorRepository.delete(collaborator);

        LocalUser localUser = localUserRepository.findByEmail(collaborator.getLocalUser().getEmail());

        return collaboratorResponse(boardId, localUser,
                collaborator.getAccessRight(), InvitationStatus.DECLINED);
    }


    public EditAccessRightDTO updateAccessRight(Claims claims, String boardId, String oid, EditAccessRightDTO editAccessRightDTO) {
        // token.is.valid AND board($id).NOT.exists
        Board board = authorizationService.getBoardId(boardId);
        String boardOid = (String) claims.get("oid");

        // owner can edit access right of collaborator
        if (boardOid.equals(board.getOwnerId())) {
            // Get collaborator or throw exception if not found
            Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(boardId, oid);
            if (editAccessRightDTO.getAccessRight() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right cannot be null");
            }
            if (collaborator != null) {
                // access_right not {READ, WRITE}:
                if (!editAccessRightDTO.getAccessRight().equalsIgnoreCase("READ") && !editAccessRightDTO.getAccessRight().equalsIgnoreCase("WRITE")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access right must be either 'READ' or 'WRITE'");
                }
                collaborator.setAccessRight(editAccessRightDTO.getAccessRight().toUpperCase());
                collaboratorRepository.save(collaborator);

                // response DTO
                EditAccessRightDTO editDtoResponse = new EditAccessRightDTO();
                editDtoResponse.setAccessRight(collaborator.getAccessRight());
                return editDtoResponse;
            } else {
                // Collaborator not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found on the board with ID: " + boardId);
            }
        } else {
            // token.is.valid AND board($id).exists AND token.oid.is.NOT.board.owner return 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Board Owner");
        }
    }

    public CollaboratorDTORes removeCollaborator(Claims claims, String boardId, String oid) {
//        token.is.valid AND board($id).NOT.exists
        Board board = authorizationService.getBoardId(boardId);
        String boardOid = (String) claims.get("oid");
        Boolean currentUserCollaboratorOpt = collaboratorRepository.findByBoardIdAndLocalUserOid(boardId, boardOid).isPresent();

        if (!currentUserCollaboratorOpt && !boardOid.equals(board.getOwnerId())) {
            // The user is neither the board owner nor a collaborator
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are neither the board owner nor a collaborator and cannot perform this action.");
        }

        Collaborator collaborator = collaboratorRepository.findByBoardIdAndLocalUserOidOrThrow(boardId, oid);
        // owner and collaborator themselves can remove collaborator
        if (boardOid.equals(board.getOwnerId()) || boardOid.equals(oid)) {
            if (collaborator != null) {
                LocalUser localUser = collaborator.getLocalUser();
                collaboratorRepository.delete(collaborator);
                return getCollabResDTO(collaborator, localUser);
            } else {
                // Collaborator not found
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found on the board with ID: " + boardId);
            }
        } else {
            // token.is.valid AND board($id).exists AND
            // (token.oid.is.NOT.board.owner OR token.oid.is.NOT.board.collaborator
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are neither the board owner nor a collaborator");
        }
    }

    private CollaboratorDTORes getCollabResDTO(Collaborator collaborator, LocalUser localUser) {
        CollaboratorDTORes collabDTO = new CollaboratorDTORes();
        // set fields from localUser
        collabDTO.setOid(localUser.getOid());
        collabDTO.setName(localUser.getName());
        collabDTO.setEmail(localUser.getEmail());
        // set fields from collaborator
        collabDTO.setAccessRight(collaborator.getAccessRight());
        collabDTO.setInvitationStatus(collaborator.getInvitationStatus());
        collabDTO.setAddedOn(collaborator.getAddedOn());
        return collabDTO;
    }

    private void validateAccess(Claims claims, Board board) {
        String oid = (String) claims.get("oid");
        boolean isOwner = oid.equals(board.getOwnerId());
        // try to understand this condition
        boolean isCollaborator = collaboratorRepository.findByBoardIdAndLocalUserOid(board.getId(), oid).isPresent();
        System.out.println("dfs" + isCollaborator);
        if (!isOwner && !isCollaborator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access board: board visibility is PRIVATE");
        }
    }

    private NewCollabDTORes collaboratorResponse(String boardId, LocalUser localUser, String accessRight, InvitationStatus invitationStatus) {
        NewCollabDTORes dtoResponse = new NewCollabDTORes();
        dtoResponse.setBoardId(boardId);
        dtoResponse.setCollaboratorName(localUser.getName());
        dtoResponse.setCollaboratorEmail(localUser.getEmail());
        dtoResponse.setAccessRight(accessRight);
        dtoResponse.setInvitationStatus(invitationStatus);
        return dtoResponse;
    }
}
