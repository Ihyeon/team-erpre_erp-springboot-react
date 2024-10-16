package com.project.erpre.model.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

// 복합키 정의
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MessageRecipientId implements Serializable {
    private Long messageNo;
    private String recipientId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageRecipientId that = (MessageRecipientId) o;
        return Objects.equals(messageNo, that.messageNo) &&
                Objects.equals(recipientId, that.recipientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageNo, recipientId);
    }
}
