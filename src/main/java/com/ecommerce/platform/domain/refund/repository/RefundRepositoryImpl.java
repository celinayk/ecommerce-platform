package com.ecommerce.platform.domain.refund.repository;

import com.ecommerce.platform.domain.refund.entity.Refund;
import com.ecommerce.platform.domain.refund.entity.RefundStatus;
import com.ecommerce.platform.domain.refund.mapper.RefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefundRepositoryImpl implements RefundRepository {

  private final RefundMapper refundMapper;

  @Override
  public Refund save(Refund refund) {
    if(refund.getId() == null) {
      // 새로운 환불 생성
      refundMapper.insert(refund);
    } else {
      refundMapper.update(refund);
    }
    return refund;
  }

  @Override
  public Optional<Refund> findById(Long id) {
    return Optional.ofNullable(refundMapper.findById(id));
  }

  @Override
  public List<Refund> findAll() {
    return refundMapper.findAll();
  }

  @Override
  public Page<Refund> findAll(Pageable pageable) {
    int offset = (int) pageable.getOffset();
    int limit = pageable.getPageSize();

    List<Refund> refunds = refundMapper.findAllWithPaging(offset, limit);
    int total = refundMapper.countAll();

    return new PageImpl<>(refunds, pageable, total);
  }

  @Override
  public void deleteById(Long id) {
    refundMapper.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return refundMapper.findById(id) != null;
  }

  @Override
  public List<Refund> findByOrderId(Long orderId) {
    return refundMapper.findByOrderId(orderId);
  }

  @Override
  public List<Refund> findByUserId(Long userId) {
    return refundMapper.findByUserId(userId);
  }

  @Override
  public List<Refund> findByStatus(RefundStatus status) {
    return refundMapper.findByStatus(status);
  }

  @Override
  public List<Refund> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    return refundMapper.findByDateRange(startDate, endDate);
  }

  @Override
  public List<Refund> findPendingRefunds() {
    return refundMapper.findPendingRefunds();
  }

  @Override
  public Integer countByStatus(RefundStatus status) {
    return refundMapper.countByStatus(status);
  }

  @Override
  public List<Refund> findRecentRefundsByUserId(Long userId, int limit) {
    return refundMapper.findRecentRefundsByUserId(userId, limit);
  }
}
