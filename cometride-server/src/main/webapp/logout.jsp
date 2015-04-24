<%-------------------------------------------------------------------%>
<%-- Copyright 2013 Code Strategies                                --%>
<%-- This code may be freely used and distributed in any project.  --%>
<%-- However, please do not remove this credit if you publish this --%>
<%-- code in paper or electronic form, such as on a web site.      --%>
<%-------------------------------------------------------------------%>

<%@ page session="true"%>

User '<%=request.getRemoteUser()%>' has been logged out.

<%
   session.invalidate();
   // New location to be redirected
   String site = new String("index.html");
   response.setStatus(response.SC_MOVED_TEMPORARILY);
   response.setHeader("Location", site);
%>