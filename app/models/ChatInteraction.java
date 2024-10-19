package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_interaction")
public class ChatInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "chat_role", nullable = false, columnDefinition = "TEXT")
    private String chatRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "submitted_at", nullable = false)
    private Timestamp submittedAt;

    @Column(name = "username", nullable = false)
    private String username;
}
