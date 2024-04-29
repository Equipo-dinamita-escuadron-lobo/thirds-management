package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.thirdsmanagement.thirds.domain.model.ePersonType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "THIRDS")
public class ThirdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "th_id")
    private Long thId; 

    @Column(name = "ent_id")
    private String entId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ti_id", referencedColumnName = "ti_id")
    @Enumerated(EnumType.STRING)
    private TypeIdEntity typeId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "thirds_and_types",
        joinColumns = @JoinColumn(name = "th_id"),
        inverseJoinColumns = @JoinColumn(name = "tt_id")
    )
    @Default
    private Set<ThirdTypeEntity> thirdTypes = new HashSet<>();

    @Column(name = "th_ruth_path")
    private String rutPath; 

    @Column(name = "th_person_type")
    private ePersonType personType; 

    @Column(name = "th_names")
    private String names; 

    @Column(name = "th_last_names")
    private String lastNames; 
    
    @Column(name = "th_social_reason")
    private String socialReason; 

    @Column(name = "th_gender")
    private String gender;

    @Column(name = "th_id_number")
    private Long idNumber;

    @Column(name = "th_verification_number")
    private Long verificationNumber; 

    @Column(name = "th_state")
    private String state;

    @Column(name = "th_photo_path")
    private String photoPath;

    @Column(name = "th_country")
    private String country;

    @Column(name = "th_province")
    private String province;

    @Column(name = "th_city")
    private String city; 

    @Column(name = "th_address")
    private String address;

    @Column(name = "th_phone_number")
    private String phoneNumber; 

    @Column(name = "th_email")
    private String email; 

    @Column(name = "th_created_at")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "th_updated_at")
    @UpdateTimestamp
    private LocalDateTime updateDate;

}
