package com.kodigo.service;

import com.kodigo.model.Invoice;
import reactor.core.publisher.Mono;

public interface IInvoiceService extends ICRUD<Invoice, String> {

    Mono<byte[]> generateReport(String idInvoice);

}