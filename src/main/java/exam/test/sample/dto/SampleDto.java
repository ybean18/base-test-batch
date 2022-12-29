package exam.test.sample.dto;

import exam.test.core.common.annotation.DatabaseEncField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SampleDto {

	public int    paym_id;
	public String paym_dt;
	public String patsto_no;

	@DatabaseEncField
	public String patsto_nm;
}
