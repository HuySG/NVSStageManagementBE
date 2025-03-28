package com.nvsstagemanagement.nvs_stage_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RequestAssetCategoryId {
    @Size(max = 50)
    @NotNull
    @Column(name = "RequestId", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String requestId;
    @Size(max = 50)
    @NotNull
    @Column(name = "CategoryID", nullable = false, length = 50,columnDefinition = "nvarchar(50)")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String categoryId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestAssetCategoryId)) return false;
        RequestAssetCategoryId that = (RequestAssetCategoryId) o;
        return Objects.equals(requestId, that.requestId)
                && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, categoryId);
    }

}
