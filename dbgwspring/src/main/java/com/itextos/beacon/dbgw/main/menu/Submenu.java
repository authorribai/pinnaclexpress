package com.itextos.beacon.dbgw.main.menu;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "submenus")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Submenu {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu parentMenu;

    // Getters and setters
    // Constructor
}

