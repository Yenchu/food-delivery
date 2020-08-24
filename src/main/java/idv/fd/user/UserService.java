package idv.fd.user;

import idv.fd.user.model.User;
import idv.fd.user.dto.EditUser;
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

    @Transactional
    public User updateUser(EditUser editUser) {

        Optional<User> optUser = userRepository.findById(editUser.getUserId());
        if (optUser.isEmpty()) {
            throw new RuntimeException("user not found: " + editUser.getUserId());
        }
        User user = optUser.get();

        user.setName(editUser.getUserName());
        return user;
    }

}
