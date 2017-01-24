package io.cfp.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }


}
