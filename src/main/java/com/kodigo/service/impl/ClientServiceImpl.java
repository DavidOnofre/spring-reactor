package com.kodigo.service.impl;

import com.kodigo.model.Client;
import com.kodigo.repo.IClientRepo;
import com.kodigo.repo.IGenericRepo;
import com.kodigo.service.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDImpl<Client, String> implements IClientService {

    private final IClientRepo repo;

    @Override
    protected IGenericRepo<Client, String> getRepo() {
        return repo;
    }
}
