package exam.test.sample.mapper;

import java.util.List;

import exam.test.sample.dto.SampleDto;

public interface SampleMapper {

	public List<SampleDto> selectSampleList();
	public int insert01(List<?> list);
}