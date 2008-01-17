/*
 * Copyright (c) 2003, Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * This file creation date: 19/03/2004 - 18:37:56
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.security;

import net.jforum.security.Role;
import net.jforum.security.RoleCollection;
import net.jforum.security.RoleValueCollection;

/**
 * Base interface for security access implementation.
 * This interface should not be directly implemented. Instead,
 * the implementation should be done on the interfaces that
 * extend this interface.
 * 
 * @author Rafael Steil
 * @version $Id: SecurityDAO.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public interface SecurityDAO 
{
	/**
	 * Delete do banco as permissoes de algum usuario em especifico.
	 * 
	 * @param userId ID do usuario/contato
	 * @param groupId ID do grupo a qual o contato pertence
	 * @see #adduserPermission(int, String, String, String)
	 * @throws Exception
	 **/
	public void deleteAllRoles(int id) throws Exception;

	/**
	 * Delete do banco as permissoes de algum usuario em especifico.
	 * 
	 * @param userId ID do usuario/contato
	 * @param groupId ID do grupo a qual o contato pertence
	 * @param field ID da permissao a deletar
	 * @see #adduserPermission(int, String, String, String)
	 * @throws Exception
	 **/
	public void deleteRole(int id, String roleName) throws Exception;

	/**
	 * Adiciona uma nova permissao/restricao ao usuario.
	 * 
	 * @param userId ID do usuario/contato
	 * @param moduleName Nome do modulo
	 * @param field Nome ( ID ) da permissao 
	 * @see #addUserPermission(int, int, String, String, String)
	 * */
	public void addRole(int id, Role role) throws Exception;
	
	public void addRoleValue(int id, Role role, RoleValueCollection rvc) throws Exception;
	
	/**
	 * @param id
	 * @param roleName
	 * @param roleValues
	 * @throws Exception
	 */
	public void addRole(int id, Role role, RoleValueCollection roleValues) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public RoleCollection loadRoles(int id) throws Exception;
}
