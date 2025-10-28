package com.empresa.portal.dto;

public class ChapterDTO {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer orderNumber;
    private String contentType;
    private String contentUrl;
    private Integer durationMinutes;
    private Boolean isCompleted;
    private String videoFileName;
    private String pdfFileName;

    // Constructores
    public ChapterDTO() {
    }

    public ChapterDTO(Long id, Long courseId, String title, String description,
            Integer orderNumber, String contentType, String contentUrl,
            Integer durationMinutes, Boolean isCompleted,
            String videoFileName, String pdfFileName) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.orderNumber = orderNumber;
        this.contentType = contentType;
        this.contentUrl = contentUrl;
        this.durationMinutes = durationMinutes;
        this.isCompleted = isCompleted;
        this.videoFileName = videoFileName;
        this.pdfFileName = pdfFileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
}