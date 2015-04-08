package net.petercashel.jmsDd.auth.interfaces;

public interface IAuthDataSystem {

	public void init();
	public void save();
	public void load();
	public void shutdown();
	
	public void AddUser(String user) throws Exception;
	public void AddUser(String user, String token) throws Exception;
	
	public boolean HasUser(String user);
	
	public void DelUser(String user);
	
	public String GetToken(String user);
	public String GetTokenSalt(String user);
	public String GetSaltedToken(String user);
	
	public void ResetToken(String user);
	public void ResetTokenSalt(String user);
	
}
