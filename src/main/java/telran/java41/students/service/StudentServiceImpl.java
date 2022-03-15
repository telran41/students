package telran.java41.students.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import telran.java41.students.dao.StudentRepository;
import telran.java41.students.dto.ScoreDto;
import telran.java41.students.dto.StudentCredentialsDto;
import telran.java41.students.dto.StudentDto;
import telran.java41.students.dto.UpdateStudentDto;
import telran.java41.students.dto.exceptions.StudentNotFoundException;
import telran.java41.students.model.Student;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
	
	StudentRepository studentRepository;
	ModelMapper modelMapper;

	@Override
	public boolean addStudent(StudentCredentialsDto studentCredentialsDto) {
		if(studentRepository.existsById(studentCredentialsDto.getId())) {
			return false;
		}
		Student student = modelMapper.map(studentCredentialsDto, Student.class);
		studentRepository.save(student);
		return true;
	}

	@Override
	public StudentDto findStudent(Integer id) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));	
		return modelMapper.map(student, StudentDto.class);
	}

	@Override
	public StudentDto deleteStudent(Integer id) {
		Student student = studentRepository.findById(id).orElse(null);
		if(student == null) {
			return null;
		}
		studentRepository.deleteById(id);			
		return modelMapper.map(student, StudentDto.class);
	}

	@Override
	public StudentCredentialsDto updateStudent(Integer id, UpdateStudentDto updateStudentDto) {
		Student student = studentRepository.findById(id).orElse(null);
		if(student == null) {
			return null;
		}
		if (updateStudentDto.getName() != null) {
			student.setName(updateStudentDto.getName());
		}
		if (updateStudentDto.getPassword() != null) {
			student.setPassword(updateStudentDto.getPassword());
		}
		studentRepository.save(student);
		return modelMapper.map(student, StudentCredentialsDto.class);
	}

	@Override
	public boolean addScore(Integer id, ScoreDto scoreDto) {
		Student student = studentRepository.findById(id).orElseThrow(null);
		if(student == null) {
			return false;
		}
		boolean res = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
		studentRepository.save(student);
		return res;

	}

	@Override
	public List<StudentDto> findStudentsByName(String name) {
		return studentRepository.findByNameIgnoreCase(name)
								.map(s -> modelMapper.map(s, StudentDto.class))
								.collect(Collectors.toList());
	}

	@Override
	public long getStudentsNamesQuantity(List<String> names) {
		return studentRepository.countByNameInIgnoreCase(names);
	}

	@Override
	public List<StudentDto> getStudentsByExamScore(String exam, int score) {
		return studentRepository.findByExamAndScoreGreateEqualsThan(exam, score)
								.map(s -> modelMapper.map(s, StudentDto.class))
								.collect(Collectors.toList());
	}

}
