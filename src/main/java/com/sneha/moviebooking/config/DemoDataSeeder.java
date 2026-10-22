package com.sneha.moviebooking.config;

import com.sneha.moviebooking.entity.Booking;
import com.sneha.moviebooking.entity.BookingSeat;
import com.sneha.moviebooking.entity.BookingStatus;
import com.sneha.moviebooking.entity.Event;
import com.sneha.moviebooking.entity.EventCategory;
import com.sneha.moviebooking.entity.EventStatus;
import com.sneha.moviebooking.entity.Payment;
import com.sneha.moviebooking.entity.PaymentMethod;
import com.sneha.moviebooking.entity.PaymentStatus;
import com.sneha.moviebooking.entity.Role;
import com.sneha.moviebooking.entity.Seat;
import com.sneha.moviebooking.entity.SeatStatus;
import com.sneha.moviebooking.entity.SeatType;
import com.sneha.moviebooking.entity.Show;
import com.sneha.moviebooking.entity.ShowSeat;
import com.sneha.moviebooking.entity.ShowSeatStatus;
import com.sneha.moviebooking.entity.ShowStatus;
import com.sneha.moviebooking.entity.User;
import com.sneha.moviebooking.entity.Venue;
import com.sneha.moviebooking.entity.VenueStatus;
import com.sneha.moviebooking.repository.BookingRepository;
import com.sneha.moviebooking.repository.EventRepository;
import com.sneha.moviebooking.repository.PaymentRepository;
import com.sneha.moviebooking.repository.SeatRepository;
import com.sneha.moviebooking.repository.ShowRepository;
import com.sneha.moviebooking.repository.ShowSeatRepository;
import com.sneha.moviebooking.repository.UserRepository;
import com.sneha.moviebooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fills an empty database with realistic demo data (movies, cinemas, shows,
 * users and a few bookings) so the app has something to show.
 *
 * It runs once at startup. If the demo user "aarav.sharma@demo.eventbook.in"
 * already exists, it does nothing, so restarting the app never duplicates data.
 * Turn it off with the environment variable DEMO_DATA_ENABLED=false.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private static final String MARKER_EMAIL = "aarav.sharma@demo.eventbook.in";

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo-data.enabled:true}")
    private boolean enabled;

    @Value("${app.demo-data.password:Demo@1234}")
    private String demoPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled || userRepository.existsByEmail(MARKER_EMAIL)) {
            return;
        }
        log.info("Seeding demo data...");

        hideOldTestData();

        // ---------- Users ----------
        User aarav = user("Aarav Sharma", MARKER_EMAIL, Role.USER);
        User priya = user("Priya Reddy", "priya.reddy@demo.eventbook.in", Role.USER);
        User rohan = user("Rohan Verma", "rohan.verma@demo.eventbook.in", Role.USER);
        User organizer = user("Cineverse Entertainment", "organizer@demo.eventbook.in", Role.ORGANIZER);

        // ---------- Movies ----------
        List<Event> movies = new ArrayList<>();
        movies.add(movie("Harry Potter and the Philosopher's Stone", 152, "UA",
                "An orphaned boy discovers on his eleventh birthday that he is a wizard and begins his first year at Hogwarts School of Witchcraft and Wizardry."));
        movies.add(movie("Harry Potter and the Deathly Hallows – Part 2", 130, "UA",
                "Harry, Ron and Hermione search for the last Horcruxes as the final battle for Hogwarts begins."));
        movies.add(movie("The Lord of the Rings: The Fellowship of the Ring", 178, "UA",
                "A quiet hobbit from the Shire is given a dangerous ring and sets out with eight companions to destroy it."));
        movies.add(movie("The Lord of the Rings: The Return of the King", 201, "UA",
                "The armies of Middle-earth gather for the last stand while Frodo and Sam near Mount Doom."));
        movies.add(movie("Avengers: Endgame", 181, "UA",
                "After losing half of all life in the universe, the remaining Avengers assemble for one last mission to set things right."));
        movies.add(movie("Spider-Man: No Way Home", 148, "UA",
                "With his identity revealed, Peter Parker asks Doctor Strange for help, and a spell gone wrong opens the multiverse."));

        // ---------- Famous cinemas ----------
        // {name, address, city, state, vip, premium, regular}
        String[][] venueData = {
                {"Prasads Multiplex (IMAX)", "NTR Marg, Khairatabad", "Hyderabad", "Telangana", "450", "320", "220"},
                {"AMB Cinemas", "Sarath City Capital Mall, Gachibowli", "Hyderabad", "Telangana", "500", "350", "250"},
                {"PVR ICON Phoenix Palladium", "Phoenix Palladium, Lower Parel", "Mumbai", "Maharashtra", "650", "450", "300"},
                {"Sathyam Cinemas", "Thiru Vi Ka Road, Royapettah", "Chennai", "Tamil Nadu", "400", "280", "190"},
                {"Raj Mandir Cinema", "Bhagwan Das Road, C-Scheme", "Jaipur", "Rajasthan", "350", "250", "150"},
        };

        // Show times every day: morning, afternoon, evening (no overlap even for the 201-minute film)
        LocalTime[] slots = {LocalTime.of(10, 0), LocalTime.of(14, 30), LocalTime.of(19, 0)};
        LocalDate firstDay = LocalDate.now().plusDays(1);
        int days = 4;

        List<Show> allShows = new ArrayList<>();
        int movieIndex = 0;
        for (String[] v : venueData) {
            Venue venue = venue(v[0], v[1], v[2], v[3]);
            List<Seat> seats = generateSeats(venue, 8, 12, 2, 2); // rows A-H, 12 seats each

            for (int d = 0; d < days; d++) {
                for (LocalTime slot : slots) {
                    Event movie = movies.get(movieIndex % movies.size());
                    movieIndex++;
                    LocalDateTime start = LocalDateTime.of(firstDay.plusDays(d), slot);
                    allShows.add(show(movie, venue, organizer, seats, start,
                            new BigDecimal(v[4]), new BigDecimal(v[5]), new BigDecimal(v[6])));
                }
            }
        }

        // ---------- A few confirmed bookings so the dashboards have data ----------
        book(aarav, allShows.get(0), PaymentMethod.UPI, 4, 5);
        book(aarav, allShows.get(14), PaymentMethod.CARD, 0, 1);
        book(priya, allShows.get(2), PaymentMethod.UPI, 30, 31, 32);
        book(priya, allShows.get(27), PaymentMethod.WALLET, 50);
        book(rohan, allShows.get(5), PaymentMethod.NET_BANKING, 60, 61);
        book(rohan, allShows.get(40), PaymentMethod.UPI, 10, 11);

        log.info("Demo data ready: {} movies, {} venues, {} shows", movies.size(), venueData.length, allShows.size());
    }

    /** Hide the "Smoke Test" events/venues that the API test script created. */
    private void hideOldTestData() {
        for (Event e : eventRepository.findAll()) {
            if (e.getTitle() != null && e.getTitle().startsWith("Smoke Test")) {
                e.setStatus(EventStatus.INACTIVE);
                eventRepository.save(e);
            }
        }
        for (Venue v : venueRepository.findAll()) {
            if (v.getName() != null && v.getName().startsWith("Hall ")) {
                v.setStatus(VenueStatus.INACTIVE);
                venueRepository.save(v);
            }
        }
        for (Show s : showRepository.findAll()) {
            if (s.getEvent().getTitle() != null && s.getEvent().getTitle().startsWith("Smoke Test")) {
                s.setStatus(ShowStatus.CANCELLED);
                showRepository.save(s);
            }
        }
    }

    private User user(String name, String email, Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(demoPassword));
        u.setRole(role);
        return userRepository.save(u);
    }

    private Event movie(String title, int minutes, String rating, String description) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription(description);
        e.setCategory(EventCategory.MOVIE);
        e.setDuration(minutes);
        e.setLanguage("English");
        e.setAgeRating(rating);
        e.setStatus(EventStatus.ACTIVE);
        return eventRepository.save(e);
    }

    private Venue venue(String name, String address, String city, String state) {
        Venue v = new Venue();
        v.setName(name);
        v.setAddress(address);
        v.setCity(city);
        v.setState(state);
        v.setTotalSeats(96);
        v.setStatus(VenueStatus.ACTIVE);
        return venueRepository.save(v);
    }

    private List<Seat> generateSeats(Venue venue, int rows, int perRow, int vipRows, int premiumRows) {
        List<Seat> seats = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            SeatType type = r < vipRows ? SeatType.VIP
                    : r < vipRows + premiumRows ? SeatType.PREMIUM
                    : SeatType.REGULAR;
            for (int n = 1; n <= perRow; n++) {
                Seat seat = new Seat();
                seat.setVenue(venue);
                seat.setRowName(String.valueOf((char) ('A' + r)));
                seat.setSeatNumber(n);
                seat.setSeatType(type);
                seat.setStatus(SeatStatus.ACTIVE);
                seats.add(seat);
            }
        }
        return seatRepository.saveAll(seats);
    }

    private Show show(Event movie, Venue venue, User organizer, List<Seat> seats, LocalDateTime start,
                      BigDecimal vip, BigDecimal premium, BigDecimal regular) {
        Show show = new Show();
        show.setEvent(movie);
        show.setVenue(venue);
        show.setOrganizer(organizer);
        show.setStartTime(start);
        show.setEndTime(start.plusMinutes(movie.getDuration() + 20L)); // + interval
        show.setVipPrice(vip);
        show.setPremiumPrice(premium);
        show.setRegularPrice(regular);
        show.setStatus(ShowStatus.ACTIVE);
        show = showRepository.save(show);

        List<ShowSeat> showSeats = new ArrayList<>();
        for (Seat seat : seats) {
            ShowSeat ss = new ShowSeat();
            ss.setShow(show);
            ss.setSeat(seat);
            ss.setPrice(seat.getSeatType() == SeatType.VIP ? vip
                    : seat.getSeatType() == SeatType.PREMIUM ? premium : regular);
            ss.setStatus(ShowSeatStatus.AVAILABLE);
            showSeats.add(ss);
        }
        showSeatRepository.saveAll(showSeats);
        return show;
    }

    /** Creates a CONFIRMED booking with a successful payment for the given seat positions (0-95). */
    private void book(User user, Show show, PaymentMethod method, int... seatPositions) {
        List<ShowSeat> showSeats = showSeatRepository.findByShow(show);
        showSeats.sort((a, b) -> {
            int byRow = a.getSeat().getRowName().compareTo(b.getSeat().getRowName());
            return byRow != 0 ? byRow : a.getSeat().getSeatNumber().compareTo(b.getSeat().getSeatNumber());
        });

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.CONFIRMED);

        BigDecimal total = BigDecimal.ZERO;
        for (int pos : seatPositions) {
            ShowSeat ss = showSeats.get(pos);
            ss.setStatus(ShowSeatStatus.BOOKED);
            total = total.add(ss.getPrice());

            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setShowSeat(ss);
            booking.getBookingSeats().add(bs);
        }
        booking.setTotalAmount(total);
        booking = bookingRepository.save(booking);
        showSeatRepository.saveAll(showSeats);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(total);
        payment.setPaymentMethod(method);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().replace("-", ""));
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
