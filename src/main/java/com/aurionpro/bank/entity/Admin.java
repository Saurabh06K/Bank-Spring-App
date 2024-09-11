package com.aurionpro.bank.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name="admins")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Admin {
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int adminId;
	
	 @NotNull(message = "First name cannot be null")
	    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	    @Column
	    private String firstName;

	    @NotNull(message = "Last name cannot be null")
	    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	    @Column
	    private String lastName;

	    @NotNull(message = "Email cannot be null")
	    @Email(message = "Email should be valid")
	    @Column
	    private String email;

	    @NotNull(message = "Password cannot be null")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    @Column
	    private String password;
}
