package dalbit.adapter.rest.external.fairytale.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.fairytale.dto.response.FairytaleResponse;
import dalbit.application.rest.external.fairytale.useCase.GetFairytaleUseCase;
import dalbit.domain.fairytale.Fairytale;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/fairytale/")
public class FairytaleJpaController {

    private final GetFairytaleUseCase getFairytaleUseCase;

    @GetMapping("list")
    public ResponseEntity<ApiResponse<List<FairytaleResponse>>> getFairytaleList() {
        List<Fairytale> fairytaleList = getFairytaleUseCase.getFairytale();

        List<FairytaleResponse> responseDate = fairytaleList.stream()
            .map(FairytaleResponse::from)
            .toList();

        return ApiResponse.success(responseDate);
    }

}
