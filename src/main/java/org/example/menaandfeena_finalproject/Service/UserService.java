package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;

import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void add(User user) {
        userRepository.save(user);
    }

    public void update(Integer id, User user) {
        User old = userRepository.findUserById(id);
        if (old == null) throw new ApiException("User not found");
        old.setFullName(user.getFullName());
        old.setEmail(user.getEmail());
        old.setPassword(user.getPassword());
        old.setPhone(user.getPhone());
        old.setNationalId(user.getNationalId());
        old.setBirthDate(user.getBirthDate());
        old.setGender(user.getGender());
        old.setStatus(user.getStatus());
        old.setYearsInNeighborhood(user.getYearsInNeighborhood());
        old.setIsVerified(user.getIsVerified());
        userRepository.save(old);
    }

    public void delete(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) throw new ApiException("User not found");
        userRepository.delete(user);
    }
}
