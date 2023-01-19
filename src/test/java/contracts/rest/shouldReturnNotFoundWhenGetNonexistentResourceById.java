package contracts.rest;

import org.springframework.cloud.contract.spec.Contract;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.map;

public class shouldReturnNotFoundWhenGetNonexistentResourceById implements Supplier<Contract> {
    @Override
    public Contract get() {
        return Contract.make(contract -> {
            contract.description("Represents a not-found-resource scenario of getting a resource by id");
            contract.request(request -> {
                request.method(request.GET());
                request.url("/api/v1/resources/1999");
                request.headers(headers -> headers.accept(headers.applicationJson()));
            });
            contract.response(response -> {
                response.status(404);
                response.body(map()
                        .entry("status", "NOT_FOUND")
                        .entry("timestamp", response.regex("\\/([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])"
                                        + " " + "(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])\\/)"))
                        .entry("message", "Resource not found")
                        .entry("debugMessage", "Resource with id=1999 not found")
                );
                response.headers(headers -> headers.contentType(headers.applicationJson()));
            });
        });
    }
}
