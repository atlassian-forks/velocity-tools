/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Velocity", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.velocity.tools.struts;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import org.apache.struts.util.MessageResources;
import org.apache.struts.action.*;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.LogEnabledViewToolImpl;
import org.apache.velocity.tools.view.tools.ServletViewTool;


/** 
 * View tool that provides methods to render Struts message resources.</p>
 *
 * <p>This class is equipped to be used with a toolbox manager, for example
 * the ServletToolboxManager included with VelServlet. The class extends 
 * ServletViewToolLogger to profit from the logging facilities of that class.
 * Furthermore, this class implements interface ServletViewTool, which allows
 * a toolbox manager to pass the required context information.</p>
 *
 * <p>This class is not thread-safe by design. A new instance is needed for
 * the processing of every template request.</p>
 *
 * @author <a href="mailto:sidler@teamup.com">Gabe Sidler</a>
 *
 * @version $Id: MessageTool.java,v 1.4 2002/04/15 18:30:28 sidler Exp $
 * 
 */
public class MessageTool extends LogEnabledViewToolImpl 
    implements ServletViewTool
{

    // --------------------------------------------- Properties -------

    /**
     * A reference to the ServletContext
     */ 
    protected ServletContext application;


    /**
     * A reference to the HttpServletRequest.
     */ 
    protected HttpServletRequest request;
    

    /**
     * A reference to the HtttpSession.
     */ 
    protected HttpSession session;


    /**
     * A reference to the Struts message resources.
     */
    protected MessageResources resources;


    /**
     * A reference to the user's locale.
     */
    protected Locale locale;


    
    // --------------------------------------------- Constructors -------------

    /**
     * Returns a factory for instances of this class. Use method 
     * {@link #getInstance(ViewContext context)} to obtain instances 
     * of this class. Do not use instance obtained from this method
     * in templates. They are not properly initialized.
     */
    public MessageTool()
    {
    }
    
    
    /**
     * For internal use only! Use method {@link #getInstance(ViewContext context)} 
     * to obtain instances of the tool.
     *
     * @param context the Velocity context
     */
    private MessageTool(ViewContext context)
    {
        this.request = context.getRequest();
        this.session = request.getSession(false);
        this.application = context.getServletContext();    
        
        resources = StrutsUtils.getMessageResources(application);
        locale = StrutsUtils.getLocale(request, session);
    }
    


    // ----------------------------------- Interface ServletViewTool -------

    /**
     * Returns an initialized instance of this view tool.
     */
    public Object getInstance(ViewContext context)
    {
        return new MessageTool(context);
    }


    /**
     * <p>Returns the default life cycle for this tool. This is 
     * {@link ServletViewTool#REQUEST}. Do not overwrite this
     * per toolbox configuration. No alternative life cycles are 
     * supported by this tool</p>
     */
    public String getDefaultLifecycle()
    {
        return ServletViewTool.REQUEST; 
    }



    // --------------------------------------------- View Helpers -------------

    /**
     * Looks up and returns the localized message for the specified key.
     * The user's locale is consulted to determine the language of the 
     * message.
     *
     * @param key message key
     *
     * @return the localized message for the specified key or 
     * <code>null</code> if no such message exists
     */
    public String get(String key)
    {
        if (resources == null)
        {
            log(ERROR, "Message resources are not available.");
            return null;
        }
        return resources.getMessage(locale, key);
    }


    /**
     * Looks up and returns the localized message for the specified key.
     * Replacement parameters passed with <code>args</code> are 
     * inserted into the message. The user's locale is consulted to 
     * determine the language of the message.
     *
     * @param key message key
     * @param args replacement parameters for this message
     *
     * @return the localized message for the specified key or 
     * <code>null</code> if no such message exists
     */
    public String get(String key, Object args[])
    {
        if (resources == null)
        {
            log(ERROR, "Message resources are not available.");
            return null;
        }
        
        // return the requested message
        if (args == null)
        {
            return resources.getMessage(locale, key);
        }
        else
        {
            return resources.getMessage(locale, key, args);
        }
    }


    /**
     * Same as {@link #get(String key, Object[] args)}, but takes a
     * <code>java.util.List</code> instead of an array. This is more
     * Velocity friendly.
     *
     * @param key message key
     * @param args replacement parameters for this message
     *
     * @return the localized message for the specified key or
     * <code>null</code> if no such message exists
     */
    public String get(String key, List args)
    {
        return get(key, args.toArray());        
    }


    /**
     * Checks if a message string for a specified message key exists
     * for the user's locale.
     *
     * @param key message key
     *
     * @return <code>true</code> if a message strings exists, 
     * <code>false</code> otherwise
     */
    public boolean exists(String key)
    {
        if (resources == null)
        {
            log(ERROR, "Message resources are not available.");
            return false;
        }

        // Return the requested message presence indicator
        return (resources.isPresent(locale, key));
    }


    /**
     * Returns the user's locale. If a locale is not found, the default 
     * locale is returned.
     */
    public Locale getLocale()
    {
        return locale;
    }
    
}