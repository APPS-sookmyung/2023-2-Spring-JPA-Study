package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String Hello(Model model){ // model : 어떤 데이터를 실어서 view에 넘길 수 있음
        model.addAttribute("data","hello!!!");
        return "hello"; // 화면 이름 resources/templates/hello.html
    }
}
