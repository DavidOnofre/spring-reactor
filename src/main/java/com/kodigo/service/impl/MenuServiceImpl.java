package com.kodigo.service.impl;

import com.kodigo.model.Menu;
import com.kodigo.repo.IGenericRepo;
import com.kodigo.repo.IMenuRepo;
import com.kodigo.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService {

    private final IMenuRepo repo;

    @Override
    protected IGenericRepo<Menu, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Menu> getMenus(String[] roles) {
        return repo.getMenus(roles);
    }

}
