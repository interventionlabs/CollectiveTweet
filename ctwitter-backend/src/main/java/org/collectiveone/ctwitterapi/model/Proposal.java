package org.collectiveone.ctwitterapi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.collectiveone.ctwitterapi.dtos.ProposalDto;

@Entity
public class Proposal {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private AppUser creator;
	@ManyToOne
	private Account account;
	private String firstVersion; 
	
	public ProposalDto toDto() {
		ProposalDto dto = new ProposalDto();
		
		dto.setId(id);
		dto.setCreatorId(creator.getId());
		dto.setFirstVersion(firstVersion);
		dto.setAccountId(account.getId());
		
		return dto;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public AppUser getCreator() {
		return creator;
	}
	public void setCreator(AppUser creator) {
		this.creator = creator;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public String getFirstVersion() {
		return firstVersion;
	}
	public void setFirstVersion(String firstVersion) {
		this.firstVersion = firstVersion;
	}
	
	
}
