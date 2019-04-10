/**
 * Handle all the validations in the landing page - login and register
 */

function characterDigit(field)
{
    var pattern = /^[a-zA-Z]{1,}$/;
    var span=field.nextSibling;

    if (!field.value.trim().match(pattern))
    {
        span.style.color="red";
        span.innerHTML="Only letters for Name!";

        field.value = '';
        //field.focus();
        return false;
    }else {
        //span.style.color="green";
        span.innerHTML="";
        return true;
    }
}

function checkUsernameLogin(field)
{
    var pattern=/^[a-zA-Z0-9]{4,}$/;
    var span=field.nextSibling;

    if(!field.value.trim().match(pattern)){
        span.style.color="red";
        span.innerHTML="Only >4 characters of letter and number!";
        field.value='';
        //field.focus();
        return false;
    }
    else{
        //span.style.color="green";
        span.innerHTML="";
        return true;
    }
}

function checkUsernameRegister(field)
{
    var pattern = /^[a-zA-Z0-9]{4,}$/;
    var span = field.nextSibling;

    for(var i = 0; i < user.length; i++)
    {
        if(field.value === user[i].username)
        {
            span.style.color = "red";
            span.innerHTML = "Username has been used!";
            field.value = "";
            return false;
        }
    }

    if(!field.value.trim().match(pattern))
    {
        span.style.color = "red";
        span.innerHTML = "Only >4 characters of letter and number!";
        field.value = "";
        return false;
    }
    else
    {
        //span.style.color = "green";
        span.innerHTML = "";
        return true;
    }
}

function checkPassword(field){

    var pattern=/^[a-zA-Z0-9]{4,}$/;
    var span=field.nextSibling;

    if(!field.value.trim().match(pattern)){
        span.style.color="red";
        span.innerHTML="Only >4 characters of letter and number!";
        field.value="";
        //field.focus();
        return true;
    }
    else{
        //span.style.color="green";
        span.innerHTML="";
        return true;
    }
}

function check2pwd(field)
{
    var span=field.nextSibling;
    var pwd = document.getElementById("passwordRegister");
    //var pwdC = document.getElementById("passwordConfirm");

    if (pwd.value.trim() !== field.value.trim())
    {
        span.style.color="red";
        span.innerHTML="Two passwords not matched!";
        //pwd.value = "";
        pwd.nextSibling.innerHTML="";
        field.value = "";
        return false;
    }
    else
    {
        //span.style.color="green";
        span.innerHTML="";
        return true;
    }
}

function checkEmail(field){
    var pattern=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var span=field.nextSibling;

    if(!field.value.trim().match(pattern)){
        span.style.color="red";
        span.innerHTML="Email format is not correct!";
        field.value='';
        //field.focus();
        return true;
    }
    else{
        //span.style.color="green";
        span.innerHTML="";
        return true;
    }
}

function checkAgreementAndPwd()
{
    var cc = document.getElementById("agree");

    if(!cc.checked)
    {
        span=cc.nextSibling;
        span.style.color="red";
        span.innerHTML="Please tick if you agree our terms!";
        return false;
    }
    return true;
}

function checkValidationLogin()
{
    var username = document.getElementById("usernameLogin");
    var password = document.getElementById("passwordLogin");
    var spanUsrN = username.nextSibling;
    var spanPwd = password.nextSibling;
    var matchUserName = false;

    for(var i = 0; i < user.length; i++)
    {
        if(username.value === user[i].username)
        {
            if(password.value !== user[i].password)
            {
                spanPwd.style.color = "red";
                spanPwd.innerHTML = "Password is not correct!";
                password.value = "";
                return false;
            }
            matchUserName = true;
        }
    }

    if(matchUserName)
        return true;
    else
    {
        spanUsrN.style.color = "red";
        spanUsrN.innerHTML = "Username does not exist!";
        username.value = "";
        password.value = "";
        return false;
    }
}