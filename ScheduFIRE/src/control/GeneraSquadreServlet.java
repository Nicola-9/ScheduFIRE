package control;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.sendmail.SendMail;

import model.bean.CapoTurnoBean;
import model.bean.ComponenteDellaSquadraBean;
import model.bean.VigileDelFuocoBean;
import model.dao.ComponenteDellaSquadraDao;
import model.dao.ListaSquadreDao;
import model.dao.SquadraDao;
import model.dao.VigileDelFuocoDao;
import util.GiornoLavorativo;
import util.Util;

/**
 * Servlet implementation class GeneraSquadreServlet
 * @author Emanuele Bombardelli
 */
@WebServlet(description = "Servlet per la generazione delle squadre", urlPatterns = { "/GeneraSquadreServlet" })
public class GeneraSquadreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GeneraSquadreServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,NotEnoughMembersException {
		Util.isCapoTurno(request);
		HttpSession sessione = request.getSession();
		Date data=new Date(System.currentTimeMillis());

		//Se è diurno, nextLavorativo restiuisce il giorno successivo, non il turno successivo
		if(GiornoLavorativo.isDiurno(data)) {
			data=Date.valueOf(LocalDate.now().plusDays(1));
		}

		data = GiornoLavorativo.nextLavorativo(data);
		Date giornoSuccessivo=Date.valueOf(data.toLocalDate().plusDays(1));


		//Se si vuole salvare la squadra sul db
		if(request.getParameter("salva")!= null) {
			@SuppressWarnings("unchecked")
			HashMap<VigileDelFuocoBean, String> squadraDiurno=(HashMap<VigileDelFuocoBean, String>)sessione.getAttribute("squadraDiurno");
			@SuppressWarnings("unchecked")
			HashMap<VigileDelFuocoBean, String> squadraNotturno=(HashMap<VigileDelFuocoBean, String>)sessione.getAttribute("squadraNotturno");
			System.out.println("squadra diurno "+squadraDiurno);
			System.out.println("squadra notturno "+squadraNotturno);
			List<ComponenteDellaSquadraBean> listaDiurno = vigileToComponente(squadraDiurno, data);	
			List<ComponenteDellaSquadraBean> listaNotturno = vigileToComponente(squadraNotturno, giornoSuccessivo);			
			CapoTurnoBean capoturno=(CapoTurnoBean)sessione.getAttribute("capoturno");


			//Se le squadre da salvare sono sul db, le rimuovo dal db restituendo i carichi di lavoro
			if(ListaSquadreDao.isEsistente(data)) {
				ArrayList<ComponenteDellaSquadraBean> componentiDiurnoDaRimuovere=ComponenteDellaSquadraDao.getComponenti(data);
				ArrayList<ComponenteDellaSquadraBean> componentiNotturnoDaRimuovere=ComponenteDellaSquadraDao.getComponenti(giornoSuccessivo);
				HashMap<VigileDelFuocoBean, String> squadraDaRimuovereDiurno=Util.ottieniSquadra(data);
				HashMap<VigileDelFuocoBean, String> squadraDaRimuovereNotturno=Util.ottieniSquadra(giornoSuccessivo);
				if(	!ComponenteDellaSquadraDao.removeComponenti(componentiDiurnoDaRimuovere))
				{
					throw new ScheduFIREException("Errore nelle Query SQL");
				}	
				if(	!ComponenteDellaSquadraDao.removeComponenti(componentiNotturnoDaRimuovere))
				{
					throw new ScheduFIREException("Errore nelle Query SQL");
				}	
				if( !VigileDelFuocoDao.removeCaricoLavorativo(squadraDaRimuovereDiurno)) 
				{
					throw new ScheduFIREException("Errore nelle Query SQL");
				}	
				if(	!VigileDelFuocoDao.removeCaricoLavorativo(squadraDaRimuovereNotturno))
				{
					throw new ScheduFIREException("Errore nelle Query SQL");
				}	

				//
				if(		(!ComponenteDellaSquadraDao.setComponenti(listaDiurno)) ||
						(!VigileDelFuocoDao.caricoLavorativo(squadraDiurno))){
					throw new ScheduFIREException("Errore nelle Query SQL");
				}	
				if(		(!ComponenteDellaSquadraDao.setComponenti(listaNotturno)) ||
						(!VigileDelFuocoDao.caricoLavorativo(squadraNotturno))){
					throw new ScheduFIREException("Errore nelle Query SQL");
				}




				//Se non sono già sul db, le salvo normalmente		
			} else {


				if((!ListaSquadreDao.aggiungiSquadre(data, capoturno.getEmail())) ||
						(!SquadraDao.aggiungiSquadra(data)) ||
						(!ComponenteDellaSquadraDao.setComponenti(listaDiurno))){
					throw new ScheduFIREException("Errore nelle Query SQL");
				}	
				VigileDelFuocoDao.caricoLavorativo(squadraDiurno);
				if((!ListaSquadreDao.aggiungiSquadre(giornoSuccessivo, capoturno.getEmail())) ||
						(!SquadraDao.aggiungiSquadra(giornoSuccessivo)) ||
						(!ComponenteDellaSquadraDao.setComponenti(listaNotturno)))
				{
					throw new ScheduFIREException("Errore nelle Query SQL");
				}
				VigileDelFuocoDao.caricoLavorativo(squadraNotturno);
			}


			//SendMail.sendMail(data);
			sessione.removeAttribute("squadraDiurno");
			sessione.removeAttribute("squadraNotturno");
			response.sendRedirect("HomeCTServlet");
			return;




		}





		System.out.println("Sto tentando di generare le squadre per il giorno "+data);

		//Se le squadre sono già state generate e sono in sessione
		if(sessione.getAttribute("squadraDiurno") != null) {
			System.out.println("Attributo squadra in sessione non nullo");
			HashMap<VigileDelFuocoBean, String> squadraDiurno=(HashMap<VigileDelFuocoBean, String>)sessione.getAttribute("squadraDiurno");
			HashMap<VigileDelFuocoBean, String> squadraNotturno=(HashMap<VigileDelFuocoBean, String>)sessione.getAttribute("squadraNotturno");
			Iterator it = squadraDiurno.entrySet().iterator();

			//Controllo se sono ancora tutti disponibili o a qualcuno è stata assegnata ferie o malattia
			//Se assegnata, cancello le squadre dalla sessione e faccio dispatch a se stessa, in modo che le rigenera
			boolean nonDisponibile=false;
			System.out.println("\n\n Squadra diurna");
			while (it.hasNext()) {
				Map.Entry coppia = (Map.Entry) it.next();
				VigileDelFuocoBean membro = (VigileDelFuocoBean) coppia.getKey();
				System.out.println("vigile "+membro.getEmail()+" squadra"+(String)coppia.getValue());
				if(!VigileDelFuocoDao.isDisponibile(membro.getEmail(), data))
					nonDisponibile=true;
			}
			it = squadraNotturno.entrySet().iterator();
			System.out.println("\n\n Squadra notturna");
			while (it.hasNext()) {
				Map.Entry coppia = (Map.Entry) it.next();
				VigileDelFuocoBean membro = (VigileDelFuocoBean) coppia.getKey();
				System.out.println("vigile "+membro.getEmail()+" squadra"+(String)coppia.getValue());

				if(!VigileDelFuocoDao.isDisponibile(membro.getEmail(), giornoSuccessivo))
					nonDisponibile=true;
			}
			if(nonDisponibile) {
				sessione.removeAttribute("squadraDiurno");
				sessione.removeAttribute("squadraNotturno");
				request.getRequestDispatcher("GeneraSquadreServlet").forward(request, response);
				return;
			}
			request.setAttribute("nonSalvata",true);
			request.getRequestDispatcher("JSP/GestioneSquadreJSP.jsp").forward(request, response);

			return;
		}


		//Se le squadre sono già nel database, non deve generare nuove squadre
		if(ListaSquadreDao.isEsistente(data)) {
			System.out.println("Squadra già esistente");
			HashMap<VigileDelFuocoBean, String> squadraDiurno=Util.ottieniSquadra(data);
			HashMap<VigileDelFuocoBean, String> squadraNotturno=Util.ottieniSquadra(giornoSuccessivo);
			sessione.setAttribute("squadraDiurno", squadraDiurno);
			sessione.setAttribute("squadraNotturno", squadraNotturno);
			request.setAttribute("nonSalvata",false);
			request.getRequestDispatcher("JSP/GestioneSquadreJSP.jsp").forward(request, response);
			return;
		}


		List<ComponenteDellaSquadraBean> listaDiurno = Util.generaSquadra(data);

		//Mi ricavo il personale disponibile restante, rimuovendo quelli schedulati
		List<VigileDelFuocoBean> disponibili=VigileDelFuocoDao.getDisponibili(giornoSuccessivo);
		for(ComponenteDellaSquadraBean componente:listaDiurno) {
			for(int i=0;i<disponibili.size();i++){
				VigileDelFuocoBean vf=disponibili.get(i);
				if(componente.getEmailVF().equals(vf.getEmail())) {
					disponibili.remove(i);
					i--;
				}

			}
		}

		disponibili.sort((VigileDelFuocoBean vf1, VigileDelFuocoBean vf2) -> 
		vf1.getCaricoLavoro() - vf2.getCaricoLavoro());


		List<ComponenteDellaSquadraBean> listaNotturno=new ArrayList<ComponenteDellaSquadraBean>();
		for(ComponenteDellaSquadraBean componente : listaDiurno) {
			if(VigileDelFuocoDao.isDisponibile(componente.getEmailVF(), giornoSuccessivo)) {
				ComponenteDellaSquadraBean comp=componente;
				comp.setGiornoLavorativo(giornoSuccessivo);
				listaNotturno.add(comp);
			}
			else {
				String mansione=VigileDelFuocoDao.ottieni(componente.getEmailVF()).getMansione();
				VigileDelFuocoBean vigileNuovo=null;
				for(VigileDelFuocoBean v :disponibili) {
					if (v.getMansione().equals(mansione)) {
						vigileNuovo=v;
						break;
					}
				}
				if(vigileNuovo!=null) {
					listaNotturno.add(new ComponenteDellaSquadraBean(componente.getTipologiaSquadra(), vigileNuovo.getEmail(), Date.valueOf(componente.getGiornoLavorativo().toLocalDate().plusDays(1))));
				}
				else
					throw new NotEnoughMembersException("Non ci sono abbastanza vigili per il turno notturno");

			}
		}

		System.out.println("Squadre generate!!!\n\n");
		System.out.println("Squadra diurna");
		for(ComponenteDellaSquadraBean comp :listaDiurno) {
			System.out.println("Squadra: "+comp.getTipologiaSquadra()+ " ,email: "+comp.getEmailVF());
		}
		System.out.println("\n\nSquadra notturna");
		for(ComponenteDellaSquadraBean comp :listaNotturno) {
			System.out.println("Squadra: "+comp.getTipologiaSquadra()+ " ,email: "+comp.getEmailVF());
		}


		HashMap<VigileDelFuocoBean, String> squadraDiurno=new HashMap<>();
		for(ComponenteDellaSquadraBean componente:listaDiurno) {
			squadraDiurno.put(VigileDelFuocoDao.ottieni(componente.getEmailVF()), componente.getTipologiaSquadra());
		}
		HashMap<VigileDelFuocoBean, String> squadraNotturno=new HashMap<>();
		for(ComponenteDellaSquadraBean componente:listaNotturno) {
			squadraNotturno.put(VigileDelFuocoDao.ottieni(componente.getEmailVF()), componente.getTipologiaSquadra());
		}
		sessione.setAttribute("squadraDiurno", squadraDiurno);
		sessione.setAttribute("squadraNotturno", squadraNotturno);
		request.setAttribute("nonSalvata",true);
		request.getRequestDispatcher("JSP/GestioneSquadreJSP.jsp").forward(request, response);













		/*
		if(sessione.getAttribute("squadra") != null) {
			@SuppressWarnings("unchecked")
			HashMap<VigileDelFuocoBean, String> squadra = (HashMap<VigileDelFuocoBean, String>) 
			sessione.getAttribute("squadra");
			List<ComponenteDellaSquadraBean> lista = vigileToComponente(squadra, data);				

			if((!ListaSquadreDao.aggiungiSquadre(data, (String) sessione.getAttribute("email"))) ||
					(!SquadraDao.aggiungiSquadra(data)) ||
					(!ComponenteDellaSquadraDao.setComponenti(lista)) ||
					(!VigileDelFuocoDao.caricoLavorativo(squadra))){
				throw new ScheduFIREException("Errore nelle Query SQL");
			}	
			SendMail.sendMail(data);
		}
		else {
			try {
				List<ComponenteDellaSquadraBean> lista = Util.generaSquadra(data);
				request.setAttribute("squadra", lista);
				request.getRequestDispatcher("/VisualizzaComposizioneSquadreServlet").forward(request, response);				
			} catch (NotEnoughMembersException e) {
				//
			} 
		}
		 */
	}


	//Trasforma l hashmap nei componenti della squadra
	private List<ComponenteDellaSquadraBean> vigileToComponente(HashMap<VigileDelFuocoBean, String> squadra, Date data) {
		List<ComponenteDellaSquadraBean> toReturn = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Iterator i = squadra.entrySet().iterator();
		while(i.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<VigileDelFuocoBean, String> coppia = (Entry<VigileDelFuocoBean, String>) i.next();
			toReturn.add(new ComponenteDellaSquadraBean(coppia.getValue(), coppia.getKey().getEmail(), data));
		}
		return toReturn;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
