package com.example.ll.finalproject.app.cart.controller;

import com.example.ll.finalproject.app.cart.entity.CartItem;
import com.example.ll.finalproject.app.cart.service.CartService;
import com.example.ll.finalproject.app.member.entity.Member;
import com.example.ll.finalproject.app.product.entity.Product;
import com.example.ll.finalproject.app.product.service.ProductService;
import com.example.ll.finalproject.app.security.dto.MemberContext;
import com.example.ll.finalproject.app.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<CartItem> items = cartService.getItemsByBuyer(buyer);
        model.addAttribute("items", items);

        return "cart/list";
    }
    @GetMapping("/add/{id}")
    @PreAuthorize("isAuthenticated()")
    public String createItem(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model){
        Member buyer = memberContext.getMember();
        Product product = productService.findById(id);
        /*
        * 나의 도서는 장바구니에 담을 수 없으며 이미 장바구니 추가된 도서는 담을 수 없다.
        */
        if(product.getAuthor().getId()==buyer.getId()){
            return "redirect:/product/list?errorMsg=" + Ut.url.encode("나의 도서는 담을 수 없습니다.");
        }
        if(cartService.isExisted(id)){
            return "redirect:/product/list?errorMsg=" + Ut.url.encode("이미 장바구니에 추가되었습니다.");
        }
        cartService.addItem(buyer, product);
        return "redirect:/product/list?msg=" + Ut.url.encode("%d건의 품목을 추가되었습니다.".formatted(1));
    }

    @PostMapping("/removeItems")
    @PreAuthorize("isAuthenticated()")
    public String removeItems(@AuthenticationPrincipal MemberContext memberContext, String ids) {
        Member buyer = memberContext.getMember();
        //카트에 담겨진 id들을 도려냄
        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findItemById(id).orElse(null);

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        cartService.removeItem(cartItem);
                    }
                });

        return "redirect:/cart/list?msg=" + Ut.url.encode("%d건의 품목을 삭제하였습니다.".formatted(idsArr.length));
    }
}
