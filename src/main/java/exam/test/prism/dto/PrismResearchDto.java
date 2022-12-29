package exam.test.prism.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PrismResearchDto {

	public String research_id;
	public String issued_year;
	public String report_open_yn;
	public int    rowNum;
	public int    file_ono;
	public String file_div;
	public String file_div_nm;
	public String file_sply_path;
	public String file_save_path;
	public String orgn_file_nm;
	public String save_file_nm;
	public String ext;
	
}
