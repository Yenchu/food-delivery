package idv.fd.user;

import idv.fd.error.AppException;
import idv.fd.user.dto.EditUser;
import idv.fd.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> findUsers(int page, int size) {

        PageRequest pr = PageRequest.of(page, size, Sort.by("name"));
        return userRepository.findAll(pr);
    }

    public User findUserById(Long id) {

        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            throw AppException.badRequest(String.format("user %d not found", id));
        }
        return optUser.get();
    }

    @Transactional
    public User findUserByIdLocked(Long id) {

        // use queryById with pessimistic lock
        Optional<User> optUser = userRepository.queryById(id);
        if (optUser.isEmpty()) {
            throw AppException.badRequest(String.format("user %d not found", id));
        }
        return optUser.get();
    }

    @Transactional
    public User updateUser(EditUser editUser) {

        User user = findUserByIdLocked(editUser.getUserId());

        user.setName(editUser.getUserName());
        return user;
    }

}
