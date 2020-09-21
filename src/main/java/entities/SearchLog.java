package entities;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "search_logs")
public class SearchLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;
	
	@Column(name = "keyword", length = 60, nullable = false)
    private String keyword;
	
	@Column(name = "searching_date")
    private Date searchingDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getSearchingDate() {
		return searchingDate;
	}

	public void setSearchingDate(Date searchingDate) {
		this.searchingDate = searchingDate;
	}
}
