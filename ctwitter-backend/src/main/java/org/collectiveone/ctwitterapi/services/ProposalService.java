package org.collectiveone.ctwitterapi.services;

import java.util.List;

import javax.transaction.Transactional;

import org.collectiveone.ctwitterapi.dtos.EditionDto;
import org.collectiveone.ctwitterapi.dtos.ProposalDto;
import org.collectiveone.ctwitterapi.dtos.TweetDto;
import org.collectiveone.ctwitterapi.model.Account;
import org.collectiveone.ctwitterapi.model.AppUser;
import org.collectiveone.ctwitterapi.model.Edition;
import org.collectiveone.ctwitterapi.model.EditionRank;
import org.collectiveone.ctwitterapi.model.EditionRankType;
import org.collectiveone.ctwitterapi.model.Proposal;
import org.collectiveone.ctwitterapi.repositories.AccountRepositoryIf;
import org.collectiveone.ctwitterapi.repositories.AppUserRepositoryIf;
import org.collectiveone.ctwitterapi.repositories.EditionRankRepositoryIf;
import org.collectiveone.ctwitterapi.repositories.EditionRepositoryIf;
import org.collectiveone.ctwitterapi.repositories.ProposalRepositoryIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProposalService {
	
	@Autowired
	AccountRepositoryIf accountRepository;
	
	@Autowired
	ProposalRepositoryIf proposalRepository;
	
	@Autowired
	EditionRepositoryIf editionRepository;
	
	@Autowired
	EditionRankRepositoryIf editionRankRepository;
	
	@Autowired
	AppUserRepositoryIf appUserRepository;
	
	@Transactional
	public String create(String userAuth0Id, Long accountId, TweetDto tweetDto) {
		Proposal proposal = new Proposal();
    	Edition edition = new Edition();
    	AppUser creator = appUserRepository.findByAuth0Id(userAuth0Id);
    	
    	Account account = accountRepository.findById(accountId);
    	
    	proposal.setCreator(creator);
    	proposal.setAccount(account);
    	proposal.setFirstVersion(tweetDto.getText());
    	
    	edition.setCreator(creator);
    	edition.setProposal(proposal);
    	edition.setText(tweetDto.getText());
    	
    	proposalRepository.save(proposal);
    	editionRepository.save(edition);
    	
    	return "success";
	}
	
	@Transactional
	public ProposalDto get(Long proposalId, String userId) {
		Proposal proposal = proposalRepository.findById(proposalId);
		List<Edition> editions = editionRepository.findByProposalId(proposal.getId());
		
		ProposalDto proposalDto = proposal.toDto();
		
		for(Edition edition : editions) {
			EditionDto editionDto = edition.toDto();
			
			if(userId != null) {
				/* add rank info if userId is not null */
				EditionRank myrank = editionRankRepository.findByEditionIdAndUserId(edition.getId(), userId);
				if(myrank != null) {
					editionDto.setMyRankType(myrank.getRankType().toString());
					editionDto.setMyRank(myrank.getRank());
				} else {
					editionDto.setMyRankType(EditionRankType.NOTRANKED.toString());
					editionDto.setMyRank(0);
				} 
			}
			
			proposalDto.getEditions().add(editionDto);
		}
		
		return proposalDto;
	}
	
	@Transactional
	public String addEdition(String userAuth0Id, Long proposalId, Long parentId, TweetDto tweetDto) {
		Proposal proposal = proposalRepository.findById(proposalId);

		Edition edition = new Edition();
		AppUser creator = appUserRepository.findByAuth0Id(userAuth0Id);
    	
    	edition.setCreator(creator);
    	edition.setProposal(proposal);
    	edition.setText(tweetDto.getText());
    	
    	if(parentId != null) {
    		edition.setParent(editionRepository.findById(parentId));	
    	}
    	
    	editionRepository.save(edition);
    	
    	return "success";
	}
	
	@Transactional
	public String rankEdition(String userId, Long proposalId, Long editionId, int rank, EditionRankType rankType) {
		
		EditionRank myrank = editionRankRepository.findByEditionIdAndUserId(editionId, userId);
    	
    	if(myrank == null) {
    		/* create new rank if not found */
    		myrank = new EditionRank();
    		myrank.setEdition(editionRepository.findById(editionId));
    		myrank.setUserId(userId);
    	}
    	
    	myrank.setRankType(rankType);
    	myrank.setRank(rank);
    	editionRankRepository.save(myrank);
    	
    	return "success";
	}
	
}
