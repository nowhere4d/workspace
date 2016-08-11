package nowhere4d.workspace.mapper;

import java.util.List;

import nowhere4d.workspace.model.User;

public interface UserMapper {
	
	public List<User> selectList();
	
	public User selectOne(Long userId);
	
	public User selectByUserName(String userName);
	
	public void insertUser(User user);
	
	public void updateUser(User user);
	
	public void deleteByUserId(Long userId);

	public void deleteAllUsers();
	
}
