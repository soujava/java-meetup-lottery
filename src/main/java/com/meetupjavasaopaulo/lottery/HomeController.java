package com.meetupjavasaopaulo.lottery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Value("classpath:meetup.xls")
    private Resource res;

    private static final String GUESTS = "guests";
    private static final String WINNERS = "winners";

    @GetMapping("/")
    public ModelAndView home(HttpSession session) {

        List<String> guests = (ArrayList<String>)session.getAttribute(GUESTS);
        Stack<String> winners = (Stack<String>)session.getAttribute(WINNERS);

        if(guests == null){
            guests = new ArrayList<>();
            winners = new Stack<>();
        }

        String winner = winners.empty()? "": winners.peek();
        ModelAndView mv = new ModelAndView("home");
        mv.addObject(GUESTS, guests);
        mv.addObject("size", guests.size());
        mv.addObject(WINNERS, winners);
        mv.addObject("winner", winner);

        return mv;

    }

    @GetMapping("/init")
    public ModelAndView startDraw(HttpSession session) {
        loadFile(session);
        session.setAttribute(WINNERS,new Stack<String>());

        return home(session);
    }

    @GetMapping("/draw")
    public ModelAndView drawNext(HttpSession session) {
        List<String> guests = (ArrayList<String>)session.getAttribute(GUESTS);
        Stack<String> winners = (Stack<String>)session.getAttribute(WINNERS);
        int winner = RandomUtil.getRandomNumberInRange(0, guests.size());
        winners.push(guests.remove(winner));

        return home(session);
    }

    private void loadFile(HttpSession session) {

        System.out.println("All files:");
        try (Stream<String> stream = Files.lines(Paths.get(res.getURI()))) {
            List<String> guests = new ArrayList<>();
            guests = stream.collect(Collectors.toList());

            System.out.println("Stream size: " + guests.size());

            session.setAttribute(GUESTS,guests);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
