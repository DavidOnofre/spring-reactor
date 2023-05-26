package com.kodigo.service.impl;

import com.kodigo.model.Invoice;
import com.kodigo.model.InvoiceDetail;
import com.kodigo.repo.IClientRepo;
import com.kodigo.repo.IDishRepo;
import com.kodigo.repo.IInvoiceRepo;
import com.kodigo.repo.IGenericRepo;
import com.kodigo.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService {

    private final IInvoiceRepo invoiceRepo;
    private final IDishRepo dishRepo;
    private final IClientRepo clientRepo;

    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)
                .flatMap(this::populateClient) // invoice -> populateClient(invoice)
                .flatMap(this::populateItems)  // invoice -> populateItems(invoice)
                .map(this::generatePdfReport)  // invoice -> generatePdfReport(invoice)
                .onErrorResume(e -> Mono.empty());
    }

    private byte[] generatePdfReport(Invoice invoice) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("txt_client", invoice.getClient().getFirstName());

            InputStream stream = getClass().getResourceAsStream("/facturas.jrxml");
            JasperReport report = JasperCompileManager.compileReport(stream);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(invoice.getItems()));
            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private Mono<Invoice> populateClient(Invoice invoice) {
        return clientRepo.findById(invoice.getClient().getId())
                .map(client -> {
                    invoice.setClient(client);
                    return invoice;
                });
    }

    private Mono<Invoice> populateItems(Invoice invoice) {
        List<Mono<InvoiceDetail>> lst = invoice.getItems().stream()
                .map(item -> dishRepo.findById(item.getDish().getId())
                        .map(dish -> {
                            item.setDish(dish);
                            return item;
                        }))
                .collect(Collectors.toList());

        return Mono.when(lst).then(Mono.just(invoice));// david
    }

    /*@Override // se refactoriza en los metodos anteriores, mismo resultado
    public Mono<byte[]> generateReport(String idInvoice) {
        return invoiceRepo.findById(idInvoice)

                //obtener Client
                .flatMap(inv -> Mono.just(inv)
                        .zipWith(clientRepo.findById(inv.getClient().getId()), (in, cl) -> {
                            in.setClient(cl);
                            return in;
                        })
                )

                //obtener Dish
                .flatMap(inv -> {
                    return Flux.fromIterable(inv.getItems())
                            .flatMap(item -> {
                                return dishRepo.findById(item.getDish().getId())
                                        .map(d -> {
                                            item.setDish(d);
                                            return item;
                                        });
                            }).collectList().flatMap(list -> {
                                inv.setItems(list);
                                return Mono.just(inv);
                            });
                })

                .map(inv -> {
                    try {
                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("txt_client", inv.getClient().getFirstName());

                        InputStream stream = getClass().getResourceAsStream("/facturas.jrxml");
                        JasperReport report = JasperCompileManager.compileReport(stream);
                        JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(inv.getItems()));
                        return JasperExportManager.exportReportToPdf(print);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new byte[0];
                });
    }*/
}
