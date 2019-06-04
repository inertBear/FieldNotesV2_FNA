package com.devhunter.fna.model;

/**
 * Data Model for a FieldNote
 * Created by DevHunter on 7/25/2018.
 */

public class FieldNote {
    private String mCreator;
    private String mProject;
    private String mWellName;
    private String mLocation;
    private String mBilling;
    //TODO: DateTime
    private String mStartDate;
    private String mEndDate;
    //TODO: int
    private String mStartTime;
    private String mEndTime;
    //TODO: int
    private String mMileageStart;
    private String mMileageEnd;
    private String mDescription;
    //TODO: GPS object
    //private String mGPS;

    private FieldNote(String creator, String project, String wellName, String location, String billing, String startDate,
                      String endDate, String startTime, String endTime, String mileageStart,
                      String mileageEnd, String description) {
        this.mCreator = creator;
        this.mProject = project;
        this.mWellName = wellName;
        this.mLocation = location;
        this.mBilling = billing;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mMileageStart = mileageStart;
        this.mMileageEnd = mileageEnd;
        this.mDescription = description;
        //this.mGPS = gps;
    }

    public String getCreator() {
        return mCreator;
    }

    public String getProject() {
        return mProject;
    }

    public String getWellName() {
        return mWellName;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getBilling() {
        return mBilling;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public String getMileageStart() {
        return mMileageStart;
    }

    public String getMileageEnd() {
        return mMileageEnd;
    }

    public String getDescription() {
        return mDescription;
    }

//    public String getGPS() {
//        return mGPS;
//    }


    public static class FieldNoteBuilder {
        private String sCreator;
        private String sProject;
        private String sWellName;
        private String sLocation;
        private String sBilling;
        //TODO: DateTime
        private String sStartDate;
        private String sEndDate;
        //TODO: int
        private String sStartTime;
        private String sEndTime;
        //TODO: int
        private String sMileageStart;
        private String sMileageEnd;
        private String sDescription;
        //TODO: GPS object
        private String sGPS;

        public FieldNoteBuilder() {

        }

        public FieldNoteBuilder setCreator(String creator) {
            this.sCreator = creator;
            return this;
        }

        public FieldNoteBuilder setProject(String project) {
            this.sProject = project;
            return this;
        }

        public FieldNoteBuilder setWellname(String wellname) {
            this.sWellName = wellname;
            return this;
        }

        public FieldNoteBuilder setLocation(String location) {
            this.sLocation = location;
            return this;
        }

        public FieldNoteBuilder setBilling(String billing) {
            this.sBilling = billing;
            return this;
        }

        public FieldNoteBuilder setDateStart(String dateStart) {
            this.sStartDate = dateStart;
            return this;
        }

        public FieldNoteBuilder setDateEnd(String dateEnd) {
            this.sEndDate = dateEnd;
            return this;
        }

        public FieldNoteBuilder setTimeStart(String timeStart) {
            this.sStartTime = timeStart;
            return this;
        }

        public FieldNoteBuilder setTimeEnd(String timeEnd) {
            this.sEndTime = timeEnd;
            return this;
        }

        public FieldNoteBuilder setMileageStart(String mileageStart) {
            this.sMileageStart = mileageStart;
            return this;
        }

        public FieldNoteBuilder setMileageEnd(String mileageEnd) {
            this.sMileageEnd = mileageEnd;
            return this;
        }

        public FieldNoteBuilder setDescription(String description) {
            this.sDescription = description;
            return this;
        }

        public FieldNoteBuilder setGPS(String gps) {
            this.sGPS = gps;
            return this;
        }

        //Excluding GPS
        public FieldNote build() {
            return new FieldNote(sCreator, sProject, sWellName, sLocation, sBilling, sStartDate,
                    sEndDate, sStartTime, sEndTime, sMileageStart,
                    sMileageEnd, sDescription);
        }
    }
}
