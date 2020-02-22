# Shopping

A toy app based on chapter 4 of Clojure Applied, which models a person going shopping with a list of things they want to buy, and a store which has an inventory of items which need to be periodically restocked.

The cart and shop should be modelled as connected components, which communicate via channels.

## The Shopper

A shopper has a list of things they need to get and a cart of items which starts empty. As they shop they will things will be removed from the list and added to the cart.

The shopper has children who help them shop. The shopper takes an item from the list, passes it to a child, who then runs off and gets the item and deposits it back into the cart. Several children can be in operation at once.

## The Shop

The shop has an inventory of items which can be fetched by shoppers. The inventory starts with 10 of each item.

Once a certain rule is met, the shop will restock, that is will buy place an order which increases all it's inventory back to 10 again.

