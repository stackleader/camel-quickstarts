Sample Request

POST /orders

{
	"firstName": "Joe",
	"lastName": "Smith",
	"cc": "123",
	"lineItems": [{
		"id": 1,
		"name": "foo",
		"quantity": 1,
		"price": 1.00
	}]
}
