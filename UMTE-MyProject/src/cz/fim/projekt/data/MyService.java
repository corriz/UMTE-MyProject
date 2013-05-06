/**
 * 
 */
package cz.fim.projekt.data;



import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

/**
 * @author Roman
 *
 */
public class MyService {

	@DatabaseField (generatedId = true)
	int id;
	
	@DatabaseField
	String name;
	
	@DatabaseField
	String text;
	
	@DatabaseField
	String corporation;
	
	@ForeignCollectionField
    private ForeignCollection<Clients> clients;
	
	public MyService(){}
	
	public MyService(String name, String text, String corporation) {
		super();
		this.name = name;
		this.text = text;
		this.corporation = corporation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String nazev) {
		this.name = nazev;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCorporation() {
		return corporation;
	}

	public void setCorporation(String vydava) {
		this.corporation = vydava;
	}

	public ForeignCollection<Clients> getClients() {
		return clients;
	}

	public void setClients(ForeignCollection<Clients> clients) {
		this.clients = clients;
	}
	
	
	
}
