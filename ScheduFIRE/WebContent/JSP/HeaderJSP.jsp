<%@ page import="control.* "%>
<%
	String ruolo = (String) session.getAttribute("ruolo");
%>

<script src="https:maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>

<div class="topnav" id="myTopnav">
<% if(ruolo!= null && ruolo.equalsIgnoreCase("capoturno")) {
%>
  <form action="HomeCTServlet" method="POST">
  <a><button class="round"><img src="IMG/logoScheduFIRE.png" class="logo">
  </button></a></form><%} else {
	  %>   <a><button class="round"><img src="IMG/logoScheduFIRE.png" class="logo"></button></a>
 <%} if(ruolo!= null){
	  %>	
 	<div class="rf">
 		<a><form action="Logout" method="POST">
 		<button class="inversesubmit" style="float:inherit;">Logout</button>
 		</form></a>
 		<% if(ruolo.equalsIgnoreCase("capoturno")){ %>
 		<a><div class="dd" >
  <button type="button" class="db" >
    <img src="IMG/notizia.png" style="height:50px; width:50px "><span class="badge">3</span>
  </button>
  <div class="ddc">
    <button class="ntf gr"  >Notifica 1</button><span class="bdgdel"><button class="nn"><img src="IMG/delete.png" class="del"></button></span>
    <button class="ntf yl" >Notifica 2</button><span class="bdgdel"><button class="nn"><img src="IMG/delete.png" class="del"></button></span>
    <button class="ntf rd" >Il personale disponibile il 03/01/2020 non è sufficiente per creare il turno.</button>
    <span class="bdgdel"><button class="nn"><img src="IMG/delete.png" class="del"></button></span>
  </div>
</div></a>

<a><div class="dd">
  <button type="button" class="db" >
    <img src="IMG/men.png" style="height:50px; width:50px ">
  </button>
  <div class="ddc">
   <form action="VisualizzaComposizioe" method="POST">
    <button class="cmd"><img src="Icon/CavallettoColorato.png" class="btl"><span class="rtlg">Gestione Squadra</span> </button>
  </form>
  <form action="CalendarioServlet" method="POST">
	<button class="cmd"><img src="Icon/calendarioColori.png" class="btl"><span class="rtlg">Visualizza Calendario</span></button>
  </form>
  <form action="GestionePersonaleServlet" method="POST">
	<button class="cmd"><img src="Icon/ominoVF.png"	class="btl"><span class="rtlg">Gestione Personale</span></button>
  </form>
  <form action="GestioneFerieServlet" method="POST">
   	<button class="cmd"><img src="Icon/solecolore.png" class="btl"><span class="rtlg">Gestione Ferie</span></button>
  </form>
  <form action="PeriodiDiMalattiaServlet" method="POST">
	<button class="cmd"><img src="Icon/MalattieColore.png" class="btl" ><span class="rtlg">Gestione Malattia</span></button>
  </form>
  <form action="PersonaleDisponibile" method="POST">
	<button class="cmd"><img src="Icon/ominiVF.png" class="btl"	><span class="rtlg">Personale Disponibile</button>
  </form>
  </div>
 
</div></a>
 		<%} %>
 	</div>
 	<%} %>
 	
</div>
