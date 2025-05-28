    package com.nvsstagemanagement.nvs_stage_management.dto.requestAsset;

    import com.nvsstagemanagement.nvs_stage_management.enums.BookingType;
    import com.nvsstagemanagement.nvs_stage_management.enums.RecurrenceType;
    import jakarta.validation.constraints.AssertTrue;
    import jakarta.validation.constraints.Max;
    import jakarta.validation.constraints.Min;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.DayOfWeek;
    import java.time.Instant;
    import java.time.LocalDate;
    import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class CreateBookingRequestDTO {
        private String title;
        private String description;
        private String assetID;
        private String taskID;
        private Instant startTime;
        private Instant endTime;
        private BookingType bookingType;
        private RecurrenceType recurrenceType;
        private Integer recurrenceInterval;
        private List<DayOfWeek> selectedDays;
        @Min(1) @Max(31)
        private Integer dayOfMonth;
        private Boolean fallbackToLastDay;
        private LocalDate recurrenceEndDate;

        @AssertTrue(message = "Recurrence end date must be after start time")
        private boolean isRecurrenceEndDateValid() {
            if (recurrenceType != RecurrenceType.NONE && recurrenceEndDate != null) {
                return recurrenceEndDate.isAfter(
                        startTime.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                );
            }
            return true;
        }

        @AssertTrue(message = "Must specify selectedDays for WEEKLY recurrence")
        private boolean isWeeklyDaysValid() {
            if (recurrenceType == RecurrenceType.WEEKLY) {
                return selectedDays != null && !selectedDays.isEmpty();
            }
            return true;
        }

        @AssertTrue(message = "Must specify dayOfMonth for MONTHLY recurrence")
        private boolean isMonthlyDayValid() {
            if (recurrenceType == RecurrenceType.MONTHLY) {
                return dayOfMonth != null && dayOfMonth >= 1 && dayOfMonth <= 31;
            }
            return true;
        }
    }
