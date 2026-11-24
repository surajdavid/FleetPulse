package com.FleetPulse.inventory.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.FleetPulse.inventory.domain.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
	List<InventoryItem> findAllByTenantIdAndWarehouseId(UUID tenantId, UUID warehouseId);
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<InventoryItem> findByTenantIdAndWarehouseIdAndSku(UUID tenantId, UUID warehouseId, String sku);
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update InventoryItem i set i.quantityReserved = i.quantityReserved + :quantity, i.version = i.version + 1 " +
			"where i.tenantId = :tenantId and i.warehouseId = :warehouseId and i.sku = :sku " +
			"and i.quantityOnHand - i.quantityReserved >= :quantity")
	int reserveIfAvailable(@Param("tenantId") UUID tenantId, @Param("warehouseId") UUID warehouseId,
			@Param("sku") String sku, @Param("quantity") long quantity);
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update InventoryItem i set i.quantityOnHand = i.quantityOnHand - :quantity, i.version = i.version + 1 " +
			"where i.tenantId = :tenantId and i.warehouseId = :warehouseId and i.sku = :sku " +
			"and i.quantityOnHand - i.quantityReserved >= :quantity")
	int issueIfAvailable(@Param("tenantId") UUID tenantId, @Param("warehouseId") UUID warehouseId,
			@Param("sku") String sku, @Param("quantity") long quantity);
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update InventoryItem i set i.quantityReserved = i.quantityReserved - :quantity, i.version = i.version + 1 " +
			"where i.tenantId = :tenantId and i.warehouseId = :warehouseId and i.sku = :sku and i.quantityReserved >= :quantity")
	int releaseReservation(@Param("tenantId") UUID tenantId, @Param("warehouseId") UUID warehouseId,
			@Param("sku") String sku, @Param("quantity") long quantity);
}
