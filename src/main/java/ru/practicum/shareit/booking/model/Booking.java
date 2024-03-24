//package ru.practicum.shareit.booking.model;
//
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//import ru.practicum.shareit.item.Item;
//import ru.practicum.shareit.user.User;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//
//@Getter
//@Setter
//@ToString
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "BOOKINGS")
//@Builder(toBuilder = true)
//@EqualsAndHashCode(of = "id")
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class Booking {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//    @Column(name = "start_time", nullable = false)
//    LocalDateTime start;
//    @Column(name = "end_time", nullable = false)
//    LocalDateTime end;
//    @ManyToOne
//    @JoinColumn(name = "item_id", nullable = false)
//    Item item;
//    @ManyToOne
//    @JoinColumn(name = "item_id", nullable = false)
//    User booker;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    BookingStatus status;
//}
