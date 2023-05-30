package com.shinhan.education.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "MEMBERS")
public class Members {
	
	private String user_name;


	private String id;

	private String password;

	private String confirmPassword;

	private String dateOfBirth;

	
	private String phoneNumber;


    private String email;
}
