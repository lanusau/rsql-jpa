package com.conversantmedia.mpub.rsql.jpa.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Getter
@Setter
@ToString
public class Car {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "model_id")
    private Long modelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    private Model model;

    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "date_sold")
    private LocalDateTime dateSold;

    @Column(name = "electric")
    private Boolean electric;

    public enum Color {
        WHITE, BLACK, RED, BLUE, SILVER
    }
}
