package rs.lukamatovic.TaskMinder.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class GenerateRandomPassService {
	
	public String generatePassword() {
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLenght = 10;
		Random random = new Random();
		
		String generatedPassword = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLenght)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		
		return generatedPassword;
	}
}
