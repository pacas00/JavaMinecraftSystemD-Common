/*******************************************************************************
 *    Copyright 2015 Peter Cashel (pacas00@petercashel.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package net.petercashel.jmsDd.auth.interfaces;

public interface IAuthDataSystem {

	public static enum permissionLevels {NOACCESS, USER, TRUSTEDUSER, MODERATOR, ADMINISTRATOR};
	
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
	
	
	public int GetPermissionLevel(String user);
	public void SetPermissionLevel(String user, int level);

}
