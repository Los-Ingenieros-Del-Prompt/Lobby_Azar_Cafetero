package com.aguardientes.azarcafetero.lobby.domain.service;

import com.aguardientes.azarcafetero.lobby.domain.model.Table;
import com.aguardientes.azarcafetero.lobby.domain.model.TableDTO;
import com.aguardientes.azarcafetero.lobby.domain.model.TableState;
import com.aguardientes.azarcafetero.lobby.domain.port.out.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetFloorTablesService unit tests")
class GetFloorTablesServiceTest {

    @Mock
    private TableRepository tableRepository;

    private GetFloorTablesService service;

    @BeforeEach
    void setUp() {
        service = new GetFloorTablesService(tableRepository);
    }

    @Test
    @DisplayName("Should return list of TableDTOs for a given floor")
    void execute_shouldReturnTableDTOs() {
        UUID floorId = UUID.randomUUID();
        Table table1 = new Table(UUID.randomUUID(), floorId, "Table 1", BigDecimal.valueOf(100), TableState.ACTIVE, 2);
        Table table2 = new Table(UUID.randomUUID(), floorId, "Table 2", BigDecimal.valueOf(200), TableState.ACTIVE, 1);
        
        when(tableRepository.findByFloorIdAndState(floorId, TableState.ACTIVE))
                .thenReturn(List.of(table1, table2));

        List<TableDTO> result = service.execute(floorId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Table 1");
        assertThat(result.get(0).getMinBet()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(result.get(1).getName()).isEqualTo("Table 2");
    }

    @Test
    @DisplayName("Should return empty list when no tables exist for floor")
    void execute_noTables_shouldReturnEmptyList() {
        UUID floorId = UUID.randomUUID();
        when(tableRepository.findByFloorIdAndState(floorId, TableState.ACTIVE))
                .thenReturn(List.of());

        List<TableDTO> result = service.execute(floorId);

        assertThat(result).isEmpty();
    }
}
