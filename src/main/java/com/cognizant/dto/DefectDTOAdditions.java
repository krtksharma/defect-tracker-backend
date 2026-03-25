package com.cognizant.dto;

// ADD these two fields to your existing DefectDTO class:

/*

    // Expected behavior (what should happen)
    private String expectedbehavior;

    // Actual behavior (what actually happens — the bug)
    private String actualbehavior;

    public String getExpectedbehavior()           { return expectedbehavior; }
    public void   setExpectedbehavior(String e)   { this.expectedbehavior = e; }

    public String getActualbehavior()             { return actualbehavior; }
    public void   setActualbehavior(String a)     { this.actualbehavior = a; }

*/

// ALSO add these two columns to your Defect entity:

/*

    @Column(name = "expectedbehavior", length = 1000)
    private String expectedbehavior;

    @Column(name = "actualbehavior", length = 1000)
    private String actualbehavior;

    public String getExpectedbehavior()           { return expectedbehavior; }
    public void   setExpectedbehavior(String e)   { this.expectedbehavior = e; }

    public String getActualbehavior()             { return actualbehavior; }
    public void   setActualbehavior(String a)     { this.actualbehavior = a; }

*/

// AND update convertToEntity in DefectServiceImpl:
/*
    d.setExpectedbehavior(dto.getExpectedbehavior());
    d.setActualbehavior(dto.getActualbehavior());
*/

// AND update convertToDTO in DefectServiceImpl:
/*
    dto.setExpectedbehavior(d.getExpectedbehavior());
    dto.setActualbehavior(d.getActualbehavior());
*/

public class DefectDTOAdditions {
    // This file is instructions only — see comments above
}
