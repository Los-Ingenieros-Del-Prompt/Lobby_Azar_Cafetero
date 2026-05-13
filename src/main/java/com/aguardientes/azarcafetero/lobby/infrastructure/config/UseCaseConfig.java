package com.aguardientes.azarcafetero.lobby.infrastructure.config;
 
import com.aguardientes.azarcafetero.lobby.domain.port.in.*;
import com.aguardientes.azarcafetero.lobby.domain.port.out.*;
import com.aguardientes.azarcafetero.lobby.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class UseCaseConfig {
 
    // — identidad —
    @Bean
    public GetPlayerIdentityUseCase getPlayerIdentityUseCase(PlayerRepository playerRepository) {
        return new GetPlayerIdentityService(playerRepository);
    }
 
    @Bean
    public GetPlayerByIdUseCase getPlayerByIdUseCase(PlayerRepository playerRepository) {
        return new GetPlayerByIdService(playerRepository);
    }
 
    @Bean
    public GetBuildingStructureUseCase getBuildingStructureUseCase(BuildingRepository buildingRepository) {
        return new GetBuildingStructureService(buildingRepository);
    }
 
    @Bean
    public GetFloorTablesUseCase getFloorTablesUseCase(TableRepository tableRepository) {
        return new GetFloorTablesService(tableRepository);
    }
 
    // — wallet (nuevos) —
    @Bean
    public GetBalanceService getBalanceService(BalanceRepository balanceRepository) {
        return new GetBalanceService(balanceRepository);
    }
 
    @Bean
    public AddDailyBonusService addDailyBonusService(BalanceRepository balanceRepository,
                                                     WalletTransactionRepository transactionRepository) {
        return new AddDailyBonusService(balanceRepository, transactionRepository);
    }
 
    @Bean
    public PlaceBetService placeBetService(BalanceRepository balanceRepository,
                                           WalletTransactionRepository transactionRepository) {
        return new PlaceBetService(balanceRepository, transactionRepository);
    }
 
    @Bean
    public ReceiveWinService receiveWinService(BalanceRepository balanceRepository,
                                               WalletTransactionRepository transactionRepository) {
        return new ReceiveWinService(balanceRepository, transactionRepository);
    }
 
    @Bean
    public RegisterLossService registerLossService(BalanceRepository balanceRepository,
                                                   WalletTransactionRepository transactionRepository) {
        return new RegisterLossService(balanceRepository, transactionRepository);
    }
 
    @Bean
    public GetTransactionHistoryService getTransactionHistoryService(
            WalletTransactionRepository transactionRepository) {
        return new GetTransactionHistoryService(transactionRepository);
    }
}
