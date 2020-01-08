<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList, model.bean.*, model.dao.*"%>
<!DOCTYPE html>
<html>
	<jsp:include page="StandardJSP.jsp" />	
	<link type="text/css" rel="stylesheet" href="./CSS/GestionePersonaleCSS.css">
	
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
	<script src="JS/datePicker.js"></script>
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script src="https://buttons.github.io/buttons.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery-modal/0.9.1/jquery.modal.min.js"></script>
	<script src="JS/ferieJS.js"></script>
	<style>
			
.table td, .table th {
    padding: 1.5px!important;
    vertical-align: top;
    border-top: 1px solid #dee2e6;
}
.back-up{
	border-radius: 50px;
    font-size: 30px;
    width: 60px;
    position: fixed;
    bottom: 5%;
    right: 5%;
    background-color:#FFFFFF;
	box-shadow: 0 8px 16px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0
		rgba(0, 0, 0, 0.19);
}
	</style>
	<body>
	<a href="#inizio"><button class=" back-up btn btn-outline-secondary"> ^ </button></a>
	<!-- Modal di aggiunta malattia-->
	<div class="modal fade" id="aggiungiMalattia" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalCenterTitle" aria-hidden="true" style="display: none">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content contenutiModal">
				<div class="modal-header">
					<h5 class="modal-title" id="titoloAggiuntaMalattia"
						>Aggiunta malattia</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<p hidden="hidden" name="email" id="emailAggiuntaMalattia"></p>
					<div class=" row justify-content-center">
						<input id="dataInizio" placeholder="Giorno iniziale" size="34" /> 
						<input id="dataFine" placeholder="Giorno finale" size="34" />
					</div>
					
				<div class="text-center" id="messaggioMalattia1"></div>
				<div class="text-center" id="messaggioMalattia2"></div>
				
				</div>

				<div class="modal-footer">
	  			<button type="button" class="btn btn-outline-primary" id="botAggiungiMalattia" 
	  			data-toggle ="modal"  data-target ="#menuConferma" 
	  			data-dismiss="modal" disabled>Aggiungi Malattia</button>
					<button type="button" class="btn btn-outline-danger" data-dismiss="modal">Annulla</button>

				</div>
			</div>
		</div>
	</div>
	
	
	<!-- Modal di avviso aggiunta malattia-->
 <div class="modal fade " id="menuConferma" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
    <div class="modal-content">

      <div class="modal-body">
        <img src="IMG/fire.png" class="rounded mx-auto d-block">
        <h4 class="modal-title text-center">Sei sicuro?</h4>
        <p class="text-center">Vuoi inserire il periodo di malattia?<br> La procedura non può essere annullata.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-danger" data-dismiss="modal">Annulla</button>
        <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"onClick = "inserisciMalattia()">inserisci</button>
      </div>
    </div>
  </div>
</div>
	
	
	<!-- Barra Navigazione -->
	<jsp:include page="HeaderJSP.jsp" />
	<h2 class="d-flex justify-content-center" style="color:#B60000!Important" id="inizio">Gestione Malattie</h2>
	
	<!-- form per l'ordinamento della lista dei VF-->
		<%
		Object ordinamentoObj = request.getAttribute("ordinamento");
		String ordinamento = (String) ordinamentoObj;
		%>
	<form action="./PeriodiDiMalattiaServlet">
			<div align="center">
				<label>Lista ordinata per</label>
				<select class="custom-select" name="ordinamento" 
				onchange="this.form.submit()"  style="width: 15%">

					<%
					if( ordinamento != null ) {
						if( ordinamento.equals("nome") ) {
					%>
					<option value="nome" selected>Nome</option>
					<option value="cognome">Cognome</option>
					<option value="mansione">mansione</option>
					<option value="grado">grado</option>
					
					<%
						} else if( ordinamento.equals("cognome") ) {		
						%>
					<option value="nome">Nome</option>
					<option value="cognome" selected>Cognome</option>
					<option value="mansione">mansione</option>
					<option value="grado">grado</option>
					<%
						} else if( ordinamento.equals("mansione") ) {		
						%>
					<option value="nome">Nome</option>
					<option value="cognome">Cognome</option>
					<option value="mansione" selected>mansione</option>
					<option value="grado">grado</option>
					<%
						} else if( ordinamento.equals("grado") ) {		
						%>
					<option value="nome">Nome</option>
					<option value="cognome">Cognome</option>
					<option value="mansione">mansione</option>
					<option value="grado" selected>grado</option>
					<%}
					}%>

				</select>
			</div>
		</form>
	
	
	<!-- body aggiungi malattia -->
	<div class="table-responsive" >
		<table class="table  table-hover" id="listaVigili">
			<thead class="thead-dark">
				<tr>
					<th class="text-center"width = 16.66%>Grado</th>
					<th class="text-center"width = 16.66%>Mansione</th>
					<th class="text-center"width = 16.66%>Nome</th>
					<th class="text-center"width = 16.66%>Cognome</th>
					<th class="text-center"width = 16.66%>Email</th>
					<th class="text-center"width = 16.66%>Inserisci Malattia</th>
				</tr>
			</thead>
				
				<tbody>
				 <%	ArrayList<VigileDelFuocoBean> listaVigili;
						listaVigili= (ArrayList<VigileDelFuocoBean>) request.getAttribute("listaVigili");
						
						for(int i=0; i<listaVigili.size(); i++){
							VigileDelFuocoBean vigile = listaVigili.get(i);
					%>
					
					<tr>
						<td class="text-center"><img src="Grado/<%=vigile.getGrado() %>.png" style="height:25%" 
						onerror="this.parentElement.innerHTML='Non disponibile';"></td>
						<td class="text-center"><%=vigile.getMansione()%></td>
						<td class="text-center"><%=vigile.getNome() %></td>
						<td class="text-center"><%=vigile.getCognome()%></td>
						<td class="text-center"><%=vigile.getEmail() %>@vigilfuoco.it</td>
						<td class="text-center"><button class="pass btn btn-outline-secondary" 
						data-toggle="modal" data-target="#aggiungiMalattia"
						onClick='apriFormAggiunta("<%=vigile.getEmail()%>")'>Aggiungi Malattia</button></td>
					</tr>
		
<% } %> 				
				
				</tbody>
			
			</table>
		</div>



<!--avvenuto inserimento-->

<div class="alert alert-success flex alert-dismissible fade in text-center fixed-top" id="inserimentoOk" 
style="display: none;position:fixed;z-index: 99999; width:100%">
  <strong>Operazione riuscita!</strong> <span>Malattia aggiunta correttamente..</span>
</div>

<!-- ----------------------- -->

<!--------- inserimento non avvenuto ----------------->

<div class="alert alert-danger flex alert-dismissible fade in text-center fixed-top" id="inserimentoNoOk" style="display: none;position:fixed;z-index: 99999; width:100%">
  <strong>Errore!</strong> <span>Aggiunta malattia non avvenuta..</span>
</div>

<!-- ----------------------- -->

		
			<script>
		
			
			var picker = new Litepicker(
					{
						element : document.getElementById('dataInizio'),
						elementEnd : document.getElementById('dataFine'),
						singleMode : false,
						format : 'DD-MM-YYYY',
						lang : 'it',
						numberOfMonths : 1,
						numberOfColumns : 1,
						inlineMode : true,
						minDate : new Date(),
						disallowLockDaysInRange : true,
						showTooltip:false,
						onError : function(error) {
							alertInsuccesso("Nel periodo selezionato risultano già dei giorni di malattia");
						},
						onSelect : function() {
						
							var differenza = calcolaGiorniMalattia($("#dataInizio").val(),$("#dataFine").val());
							
							$("#messaggioMalattia1").text("Periodo selezionato correttamente.");
							$("#messaggioMalattia2").text("Hai selezionato " + differenza + " giorni di malattia.");
							$("#messaggioMalattia1").attr("style","color:green");
							$("#messaggioMalattia2").attr("style","color:green");
							$('#botAggiungiMalattia').prop("disabled",false);
								}});	
			
			function calcolaGiorniMalattia(inizale,finale) {
				//trasformo le date nel formato aaaammgg 
				anno1 = parseInt(inizale.substr(6),10);
				mese1 = parseInt(inizale.substr(3, 2),10);
				giorno1 = parseInt(inizale.substr(0, 2),10);
			     
				anno2 = parseInt(finale.substr(6),10);
				mese2 = parseInt(finale.substr(3, 2),10);
				giorno2 = parseInt(finale.substr(0, 2),10);
				
		        data1str = anno1+mese1+giorno1;
				data2str = anno2+mese2+giorno2;
				//controllo se la date sono uguali
		        if (data2str-data1str == 0) {
		            return 1;
		        }else{

				anno1 = parseInt(inizale.substr(6),10);
				mese1 = parseInt(inizale.substr(3, 2),10);
				giorno1 = parseInt(inizale.substr(0, 2),10);
			     
				anno2 = parseInt(finale.substr(6),10);
				mese2 = parseInt(finale.substr(3, 2),10);
				giorno2 = parseInt(finale.substr(0, 2),10);

			    var dataok1=new Date(anno1, mese1-1, giorno1);
				var dataok2=new Date(anno2, mese2-1, giorno2);
				
				differenza = dataok2-dataok1;    
				giorni_differenza = new String(1+differenza/86400000);
				
				return giorni_differenza;}
			}
			
			
			function apriFormAggiunta(input) {
				console.log("parte funzione apriformAggiunta di " + input);
				picker.setOptions({
					startDate : null,
					endDate : null
				});
				$("#dataInizio").val("");
				$("#dataFine").val("");
				picker.setLockDays([]);
				$("#messaggioMalattia1").text("");
				$("#messaggioMalattia2").text("");
				
				$.ajax({
					type : "POST",
					url : "PeriodiDiMalattiaServlet",
					data : {
						"visMalattia" : true,
						"JSON" : true,
						"emailVF" : input,
					},
					dataType : "json",
					async : false,
					success : function(response) {
						picker.setLockDays(response);
               
        				var mail = document.getElementById("emailAggiuntaMalattia");
        				mail.innerHTML = input;
        				
        				var cognome = $(".table td:contains('" + input + "')").prev('td');
        				var nome = $(cognome).prev('td');
    
        			$("#titoloAggiuntaMalattia").text(
        						"Aggiunta malattia per " + nome.text() + " "+ cognome.text());
        			$('#formAggiunta').show();	
					$(".contenutiModal").css('background-color', '#e6e6e6');
					}
					
				});
			}			
			</script>
			
			<script>
			function inserisciMalattia(){
				 var emailVF = document.getElementById("emailAggiuntaMalattia").innerHTML;
				 var dataIn = $('#dataInizio').val();
				 var dataFi = $('#dataFine').val();

				 $.ajax({
				type : "POST",
				url : "AggiungiMalattiaServlet",
				data : {
					"inserisci": true, 
					"JSON" : true,
					"emailVF": emailVF,
					"dataInizio": dataIn,
					"dataFine" : dataFi
				},
						dataType : "json",
						async : false,
						
					
				 success : function(response) {
					 var Risposta=response[0];
					 if(Risposta){
						 alertSuccesso();
						 
					 }
					 else{
						 apriFormAggiunta();
						 alertInsuccesso();
					 }
				 },
			});	
		}

			</script>
			<script>
			
			function alertInsuccesso(input){
				$("#inserimentoNoOk span").text(input);
				$("#inserimentoNoOk").fadeTo(4000, 500).slideUp(500, function(){
				    $("#success-alert").slideUp(500);
				});
			}
			
			function alertSuccesso(input){
				$("#inserimentoOk span").text(input);
				$("#inserimentoOk").fadeTo(4000, 500).slideUp(500, function(){
				    $("#success-alert").slideUp(500);
				});
			}
			</script>

	</body>
</html>