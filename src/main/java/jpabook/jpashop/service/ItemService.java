package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity)
    {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        //직접 set을 호출하는 것보다 item.change 처럼 entity 내에 변경 메서드를 만드는 것이 좋음
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
