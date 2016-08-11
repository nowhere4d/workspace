package nowhere4d.workspace.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nowhere4d.workspace.mapper.UserMapper;
import nowhere4d.workspace.model.User;

@Service
@Transactional
public class UserService {

	@Autowired
	UserMapper userMapper;

	public List<User> findAllUsers() {
		return userMapper.selectList();
	}

	public User findById(long id) {
		return userMapper.selectOne(id);
	}

	public User findByName(String name) {
		return userMapper.selectByUserName(name);
	}

	public void saveUser(User user) {
		userMapper.insertUser(user);
	}

	public void updateUser(User user) {
		userMapper.updateUser(user);
	}

	public void deleteUserById(long id) {
		userMapper.deleteByUserId(id);
	}

	public boolean isUserExist(User user) {
		return findByName(user.getUsername()) != null;
	}

	public void deleteAllUsers() {
		userMapper.deleteAllUsers();
	}

}
