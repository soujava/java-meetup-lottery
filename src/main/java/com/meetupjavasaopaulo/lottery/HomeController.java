package com.meetupjavasaopaulo.lottery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.ArrayUtils;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Value("classpath:meetup.xls")
    private Resource res;

    @Autowired
    private HttpSession session;

    private static final String GUESTS = "guests";
    private static final String WINNERS = "winners";




    @GetMapping("/")
    public ModelAndView home() {

        List<String> guests = getGuests();
        Stack<String> winners = getWinners();

        String winner = winners.empty()? "Draw has not started": winners.peek();
        ModelAndView mv = new ModelAndView("home");
        mv.addObject(GUESTS, guests);
        mv.addObject("size", guests.size());
        mv.addObject(WINNERS, winners);
        mv.addObject("winner", winner);

        return mv;

    }

    @GetMapping("/init")
    public ModelAndView startDraw() {
        loadFile();

        return home();
    }

    @GetMapping("/draw")
    public ModelAndView drawNext() {
        List<String> guests = getGuests();
        Stack<String> winners = getWinners();
        int winner = RandomUtil.getRandomNumberInRange(0, guests.size());
        System.out.println("Winner index:"+ winner);
        winners.push(guests.remove(winner));

        return home();
    }

    private void loadFile() {

        System.out.println("Load guests file");
        try (Stream<String> stream = Files.lines(Paths.get(res.getURI()))) {
            List<String> guests = stream.collect(Collectors.toList());

            System.out.println("Stream size: " + guests.size());

            if(guests.get(0).contains("Name\tUser ID")) //Remove file header
                guests.remove(0);

            setGuests(guests);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private List<String> getGuests(){

        if(session.getAttribute(GUESTS) == null)
            setGuests(new ArrayList<>());

        return  (ArrayList<String>)session.getAttribute(GUESTS);
    }

    private void setGuests(List<String> guests){
        session.setAttribute(GUESTS,guests);
    }

    private Stack<String> getWinners() {
        if(session.getAttribute(WINNERS) == null)
            setWinners(new Stack<>());

        return (Stack<String>)session.getAttribute(WINNERS);
    }

    private void setWinners(Stack<String> winners) {
        session.setAttribute(WINNERS, winners);
    }

}
